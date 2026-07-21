package com.example.englishlearning;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private final String[] english={"apple","book","school","beautiful","important","family","water","teacher","morning","travel"};
    private final String[][] choices={{"蘋果","香蕉","葡萄","橘子"},{"筆","書","桌子","書包"},{"老師","學生","學校","教室"},{"快速的","美麗的","困難的","重要的"},{"簡單的","重要的","安靜的","便宜的"},{"朋友","家庭","工作","城市"},{"牛奶","咖啡","水","果汁"},{"醫生","老師","司機","警察"},{"晚上","下午","早晨","昨天"},{"閱讀","旅行","游泳","工作"}};
    private final int[] answers={0,1,2,1,1,1,2,1,2,1};
    private TextView progress,word,result,score,best; private Button[] optionButtons; private Button next,speak;
    private int current=0, points=0; private boolean answered=false; private TextToSpeech tts; private SharedPreferences prefs;

    @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("english_learning",MODE_PRIVATE);tts=new TextToSpeech(this,this);buildUi();showQuestion();}
    private void buildUi(){
        ScrollView sv=new ScrollView(this); sv.setFillViewport(true);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20),dp(20),dp(20),dp(28)); root.setBackgroundColor(Color.rgb(246,247,252)); sv.addView(root);
        TextView title=t("英文學習",30,Color.rgb(70,43,190)); title.setTypeface(Typeface.DEFAULT_BOLD); title.setGravity(Gravity.CENTER); root.addView(title,full());
        TextView sub=t("單字測驗・發音練習・成績紀錄",16,Color.DKGRAY); sub.setGravity(Gravity.CENTER); sub.setPadding(0,dp(5),0,dp(20)); root.addView(sub,full());
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(18),dp(20),dp(18),dp(20)); card.setBackgroundColor(Color.WHITE); root.addView(card,full());
        progress=t("",15,Color.DKGRAY); card.addView(progress,full());
        word=t("",34,Color.BLACK); word.setTypeface(Typeface.DEFAULT_BOLD); word.setGravity(Gravity.CENTER); word.setPadding(0,dp(24),0,dp(14)); card.addView(word,full());
        speak=btn("🔊 聽發音"); speak.setOnClickListener(v->speakWord()); card.addView(speak,full());
        TextView prompt=t("請選擇正確的中文意思",17,Color.DKGRAY); prompt.setGravity(Gravity.CENTER); prompt.setPadding(0,dp(20),0,dp(8)); card.addView(prompt,full());
        optionButtons=new Button[4]; for(int i=0;i<4;i++){final int selected=i; Button b=btn(""); LinearLayout.LayoutParams p=full(); p.setMargins(0,dp(8),0,0); b.setOnClickListener(v->check(selected)); card.addView(b,p); optionButtons[i]=b;}
        result=t("",18,Color.DKGRAY); result.setGravity(Gravity.CENTER); result.setPadding(0,dp(18),0,dp(4)); card.addView(result,full());
        score=t("",16,Color.DKGRAY); score.setGravity(Gravity.CENTER); card.addView(score,full());
        best=t("",15,Color.GRAY); best.setGravity(Gravity.CENTER); best.setPadding(0,dp(4),0,dp(10)); card.addView(best,full());
        next=btn("下一題"); next.setVisibility(View.GONE); next.setOnClickListener(v->nextQuestion()); card.addView(next,full()); setContentView(sv);
    }
    private void showQuestion(){answered=false;progress.setText("第 "+(current+1)+" 題，共 "+english.length+" 題");word.setText(english[current]);result.setText("");score.setText("目前得分："+points);best.setText("最佳成績："+prefs.getInt("best_score",0)+" / "+english.length);next.setVisibility(View.GONE);for(int i=0;i<4;i++){optionButtons[i].setText(choices[current][i]);optionButtons[i].setEnabled(true);optionButtons[i].setTextColor(Color.BLACK);optionButtons[i].setBackgroundColor(Color.rgb(235,232,250));}}
    private void check(int selected){if(answered)return;answered=true;int correct=answers[current];for(Button b:optionButtons)b.setEnabled(false);optionButtons[correct].setBackgroundColor(Color.rgb(203,240,216));optionButtons[correct].setTextColor(Color.rgb(0,110,55));if(selected==correct){points++;result.setText("答對了！");result.setTextColor(Color.rgb(0,120,60));}else{optionButtons[selected].setBackgroundColor(Color.rgb(255,220,220));optionButtons[selected].setTextColor(Color.RED);result.setText("答錯了，正確答案是「"+choices[current][correct]+"」");result.setTextColor(Color.RED);}score.setText("目前得分："+points);next.setText(current==english.length-1?"查看結果":"下一題");next.setVisibility(View.VISIBLE);}
    private void nextQuestion(){if(current<english.length-1){current++;showQuestion();}else showResult();}
    private void showResult(){int old=prefs.getInt("best_score",0);if(points>old)prefs.edit().putInt("best_score",points).apply();progress.setText("練習完成");word.setText(points+" / "+english.length);result.setText(points==english.length?"太棒了，全部答對！":points>=7?"表現很好，繼續保持！":points>=5?"不錯，再練習一次會更好！":"繼續加油，每天進步一點點！");result.setTextColor(Color.rgb(70,43,190));score.setText("最佳成績："+Math.max(points,old)+" / "+english.length);best.setText("");speak.setVisibility(View.GONE);for(Button b:optionButtons)b.setVisibility(View.GONE);next.setText("重新練習");next.setVisibility(View.VISIBLE);next.setOnClickListener(v->restart());}
    private void restart(){current=0;points=0;speak.setVisibility(View.VISIBLE);for(Button b:optionButtons)b.setVisibility(View.VISIBLE);next.setOnClickListener(v->nextQuestion());showQuestion();}
    private void speakWord(){if(tts!=null)tts.speak(english[current],TextToSpeech.QUEUE_FLUSH,null,"word");}
    @Override public void onInit(int status){if(status==TextToSpeech.SUCCESS){int r=tts.setLanguage(Locale.US);if(r==TextToSpeech.LANG_MISSING_DATA||r==TextToSpeech.LANG_NOT_SUPPORTED)Toast.makeText(this,"裝置不支援英文語音",Toast.LENGTH_SHORT).show();}}
    @Override protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
    private TextView t(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);return v;}
    private Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextSize(17);b.setAllCaps(false);b.setPadding(dp(12),dp(10),dp(12),dp(10));return b;}
    private LinearLayout.LayoutParams full(){return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
