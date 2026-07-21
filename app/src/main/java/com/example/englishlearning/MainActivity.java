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

public class MainActivity extends Activity
        implements TextToSpeech.OnInitListener {

    /*
     * 英文單字
     */
    private final String[] words = {
            "apple",
            "book",
            "school",
            "beautiful",
            "important",
            "family",
            "water",
            "teacher",
            "morning",
            "travel"
    };

    /*
     * 各題選項
     */
    private final String[][] choices = {
            {"蘋果", "香蕉", "葡萄", "橘子"},
            {"筆", "書", "桌子", "書包"},
            {"老師", "學生", "學校", "教室"},
            {"快速的", "美麗的", "困難的", "重要的"},
            {"簡單的", "重要的", "安靜的", "便宜的"},
            {"朋友", "家庭", "工作", "城市"},
            {"牛奶", "咖啡", "水", "果汁"},
            {"醫生", "老師", "司機", "警察"},
            {"晚上", "下午", "早晨", "昨天"},
            {"閱讀", "旅行", "游泳", "工作"}
    };

    /*
     * 正確答案的位置
     * 0 代表第一個選項
     * 1 代表第二個選項
     * 2 代表第三個選項
     * 3 代表第四個選項
     */
    private final int[] correctAnswers = {
            0,
            1,
            2,
            1,
            1,
            1,
            2,
            1,
            2,
            1
    };

    /*
     * 畫面元件
     */
    private TextView progressText;
    private TextView wordText;
    private TextView resultText;
    private TextView scoreText;
    private TextView bestScoreText;

    private Button speakButton;
    private Button nextButton;
    private Button[] answerButtons;

    /*
     * 測驗狀態
     */
    private int currentQuestion = 0;
    private int score = 0;
    private boolean answered = false;

    /*
     * 英文發音
     */
    private TextToSpeech textToSpeech;

    /*
     * 儲存最佳成績
     */
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(
                "english_learning",
                MODE_PRIVATE
        );

        textToSpeech = new TextToSpeech(
                this,
                this
        );

        createScreen();
        showQuestion();
    }

    /*
     * 建立整個 App 畫面
     */
    private void createScreen() {

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.setFillViewport(true);

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(20),
                dp(24),
                dp(20),
                dp(30)
        );

        root.setBackgroundColor(
                Color.rgb(246, 247, 252)
        );

        scrollView.addView(root);

        /*
         * 主標題
         */
        TextView titleText = createTextView(
                "英文學習",
                30,
                Color.rgb(91, 63, 214)
        );

        titleText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        titleText.setGravity(
                Gravity.CENTER
        );

        root.addView(
                titleText,
                fullWidth()
        );

        /*
         * 副標題
         */
        TextView subtitleText =
                createTextView(
                        "English Learning",
                        16,
                        Color.DKGRAY
                );

        subtitleText.setGravity(
                Gravity.CENTER
        );

        subtitleText.setPadding(
                0,
                dp(6),
                0,
                dp(22)
        );

        root.addView(
                subtitleText,
                fullWidth()
        );

        /*
         * 白色內容區塊
         */
        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(18),
                dp(20),
                dp(18),
                dp(22)
        );

        card.setBackgroundColor(
                Color.WHITE
        );

        root.addView(
                card,
                fullWidth()
        );

        /*
         * 題目進度
         */
        progressText = createTextView(
                "",
                15,
                Color.DKGRAY
        );

        card.addView(
                progressText,
                fullWidth()
        );

        /*
         * 英文單字
         */
        wordText = createTextView(
                "",
                36,
                Color.BLACK
        );

        wordText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        wordText.setGravity(
                Gravity.CENTER
        );

        wordText.setPadding(
                0,
                dp(25),
                0,
                dp(18)
        );

        card.addView(
                wordText,
                fullWidth()
        );        /*
         * 發音按鈕
         */
        speakButton = createButton("🔊 聽英文發音");

        speakButton.setOnClickListener(
                view -> speakCurrentWord()
        );

        card.addView(
                speakButton,
                fullWidth()
        );

        /*
         * 題目提示
         */
        TextView questionTitle =
                createTextView(
                        "請選擇正確的中文意思",
                        18,
                        Color.DKGRAY
                );

        questionTitle.setGravity(
                Gravity.CENTER
        );

        questionTitle.setPadding(
                0,
                dp(22),
                0,
                dp(10)
        );

        card.addView(
                questionTitle,
                fullWidth()
        );

        /*
         * 四個答案按鈕
         */
        answerButtons = new Button[4];

        for (int i = 0; i < answerButtons.length; i++) {

            final int selected = i;

            Button button =
                    createButton("");

            LinearLayout.LayoutParams params =
                    fullWidth();

            params.setMargins(
                    0,
                    dp(10),
                    0,
                    0
            );

            button.setOnClickListener(
                    view -> checkAnswer(selected)
            );

            card.addView(
                    button,
                    params
            );

            answerButtons[i] = button;
        }

        /*
         * 答題結果
         */
        resultText =
                createTextView(
                        "",
                        18,
                        Color.BLACK
                );

        resultText.setGravity(
                Gravity.CENTER
        );

        resultText.setPadding(
                0,
                dp(20),
                0,
                dp(5)
        );

        card.addView(
                resultText,
                fullWidth()
        );

        /*
         * 分數
         */
        scoreText =
                createTextView(
                        "",
                        16,
                        Color.DKGRAY
                );

        scoreText.setGravity(
                Gravity.CENTER
        );

        card.addView(
                scoreText,
                fullWidth()
        );

        /*
         * 最佳成績
         */
        bestScoreText =
                createTextView(
                        "",
                        15,
                        Color.GRAY
                );

        bestScoreText.setGravity(
                Gravity.CENTER
        );

        bestScoreText.setPadding(
                0,
                dp(5),
                0,
                dp(12)
        );

        card.addView(
                bestScoreText,
                fullWidth()
        );

        /*
         * 下一題按鈕
         */
        nextButton =
                createButton("下一題");

        nextButton.setVisibility(
                View.GONE
        );

        nextButton.setOnClickListener(
                view -> nextQuestion()
        );

        card.addView(
                nextButton,
                fullWidth()
        );

        /*
         * 顯示畫面
         */
        setContentView(scrollView);
    }

    /*
     * 顯示目前題目
     */
    private void showQuestion() {

        answered = false;

        progressText.setText(
                "第 "
                        + (currentQuestion + 1)
                        + " 題，共 "
                        + words.length
                        + " 題"
        );

        wordText.setText(
                words[currentQuestion]
        );

        resultText.setText("");

        scoreText.setText(
                "目前得分：" + score
        );

        int best =
                preferences.getInt(
                        "best_score",
                        0
                );

        bestScoreText.setText(
                "最佳成績："
                        + best
                        + " / "
                        + words.length
        );

        nextButton.setVisibility(
                View.GONE
        );

        for (int i = 0; i < 4; i++) {

            Button button =
                    answerButtons[i];

            button.setEnabled(true);

            button.setText(
                    choices[currentQuestion][i]
            );

            button.setBackgroundColor(
                    Color.rgb(
                            235,
                            232,
                            250
                    )
            );

            button.setTextColor(
                    Color.BLACK
            );
        }
    }    /*
     * 檢查答案
     */
    private void checkAnswer(int selected) {

        if (answered) {
            return;
        }

        answered = true;

        for (Button button : answerButtons) {
            button.setEnabled(false);
        }

        int correct = correctAnswers[currentQuestion];

        answerButtons[correct].setBackgroundColor(
                Color.rgb(204, 240, 210)
        );

        answerButtons[correct].setTextColor(
                Color.rgb(0,120,60)
        );

        if (selected == correct) {

            score++;

            resultText.setText("✓ 答對了！");

            resultText.setTextColor(
                    Color.rgb(0,120,60)
            );

        } else {

            answerButtons[selected].setBackgroundColor(
                    Color.rgb(255,220,220)
            );

            answerButtons[selected].setTextColor(
                    Color.RED
            );

            resultText.setText(
                    "✗ 答錯了！\n正確答案：" +
                            choices[currentQuestion][correct]
            );

            resultText.setTextColor(
                    Color.RED
            );
        }

        scoreText.setText(
                "目前得分：" + score
        );

        if (currentQuestion == words.length - 1) {
            nextButton.setText("查看成績");
        } else {
            nextButton.setText("下一題");
        }

        nextButton.setVisibility(
                View.VISIBLE
        );
    }

    /*
     * 下一題
     */
    private void nextQuestion() {

        currentQuestion++;

        if (currentQuestion >= words.length) {

            showFinalResult();

            return;
        }

        showQuestion();
    }

    /*
     * 顯示最終成績
     */
    private void showFinalResult() {

        int best =
                preferences.getInt(
                        "best_score",
                        0
                );

        if (score > best) {

            preferences.edit()
                    .putInt(
                            "best_score",
                            score
                    )
                    .apply();

            best = score;
        }

        progressText.setText("🎉 測驗完成");

        wordText.setText(
                score + " / " + words.length
        );

        if (score == words.length) {

            resultText.setText(
                    "太厲害了！全部答對！"
            );

        } else if (score >= 8) {

            resultText.setText(
                    "非常棒！繼續保持！"
            );

        } else if (score >= 6) {

            resultText.setText(
                    "表現不錯，再接再厲！"
            );

        } else {

            resultText.setText(
                    "多練習幾次，一定會進步！"
            );
        }

        scoreText.setText(
                "最佳成績：" +
                        best +
                        " / " +
                        words.length
        );

        bestScoreText.setText("");

        speakButton.setVisibility(
                View.GONE
        );

        for (Button button : answerButtons) {
            button.setVisibility(
                    View.GONE
            );
        }

        nextButton.setVisibility(
                View.VISIBLE
        );

        nextButton.setText("重新開始");

        nextButton.setOnClickListener(
                view -> restartQuiz()
        );
    }    /*
     * 重新開始測驗
     */
    private void restartQuiz() {

        currentQuestion = 0;
        score = 0;
        answered = false;

        speakButton.setVisibility(
                View.VISIBLE
        );

        for (Button button : answerButtons) {

            button.setVisibility(
                    View.VISIBLE
            );
        }

        nextButton.setOnClickListener(
                view -> nextQuestion()
        );

        showQuestion();
    }

    /*
     * 播放目前英文單字
     */
    private void speakCurrentWord() {

        if (textToSpeech == null) {
            return;
        }

        textToSpeech.speak(
                words[currentQuestion],
                TextToSpeech.QUEUE_FLUSH,
                null,
                "english_word"
        );
    }

    /*
     * TextToSpeech 初始化完成
     */
    @Override
    public void onInit(int status) {

        if (status != TextToSpeech.SUCCESS) {

            Toast.makeText(
                    this,
                    "英文語音初始化失敗",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int result =
                textToSpeech.setLanguage(
                        Locale.US
                );

        if (result ==
                TextToSpeech.LANG_MISSING_DATA
                ||
                result ==
                        TextToSpeech.LANG_NOT_SUPPORTED) {

            Toast.makeText(
                    this,
                    "裝置未安裝英文語音資料",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /*
     * 關閉 App 時釋放語音資源
     */
    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }

    /*
     * 建立文字元件
     */
    private TextView createTextView(
            String text,
            int textSize,
            int textColor
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);

        return textView;
    }

    /*
     * 建立按鈕
     */
    private Button createButton(
            String text
    ) {

        Button button =
                new Button(this);

        button.setText(text);
        button.setTextSize(17);
        button.setAllCaps(false);

        button.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        return button;
    }

    /*
     * 建立滿寬版面參數
     */
    private LinearLayout.LayoutParams fullWidth() {

        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    /*
     * dp 轉換成 px
     */
    private int dp(int value) {

        return Math.round(
                value
                        *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}
