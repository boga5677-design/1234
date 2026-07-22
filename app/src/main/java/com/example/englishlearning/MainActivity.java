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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity
        implements TextToSpeech.OnInitListener {

    private static final int QUESTION_COUNT = 10;
    private static final int OPTION_COUNT = 4;

    private TextView progressText;
    private TextView wordText;
    private TextView resultText;
    private TextView scoreText;
    private TextView bestScoreText;

    private Button speakButton;
    private Button nextButton;
    private Button[] answerButtons;

    private final List<Vocabulary.Word> quizWords = new ArrayList<>();
    private final String[] currentChoices = new String[OPTION_COUNT];

    private int currentQuestion = 0;
    private int correctAnswerIndex = 0;
    private int score = 0;
    private boolean answered = false;

    private TextToSpeech textToSpeech;
    private SharedPreferences preferences;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        random = new Random();

        preferences = getSharedPreferences(
                "english_learning",
                MODE_PRIVATE
        );

        textToSpeech = new TextToSpeech(
                this,
                this
        );

        prepareQuiz();
        createScreen();
        showQuestion();
    }

    /*
     * 隨機產生本次測驗題目
     */
    private void prepareQuiz() {

        quizWords.clear();

        List<Vocabulary.Word> allWords =
                new ArrayList<>();

       try {

    allWords.addAll(
            Vocabulary.load(this)
    );

} catch (IOException error) {

    Toast.makeText(
            this,
            "題庫載入失敗："
                    + error.getMessage(),
            Toast.LENGTH_LONG
    ).show();

    return;
}
            
            import java.io.IOException;
        Collections.shuffle(allWords);

        int count = Math.min(
                QUESTION_COUNT,
                allWords.size()
        );

        quizWords.addAll(
                allWords.subList(0, count)
        );
    }

    /*
     * 建立畫面
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

        TextView titleText = createTextView(
                "TOEIC 英文學習",
                30,
                Color.rgb(91, 63, 214)
        );

        titleText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        titleText.setGravity(Gravity.CENTER);

        root.addView(
                titleText,
                fullWidth()
        );

        TextView subtitleText = createTextView(
                "多益單字隨機測驗",
                16,
                Color.DKGRAY
        );

        subtitleText.setGravity(Gravity.CENTER);

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

        card.setBackgroundColor(Color.WHITE);

        root.addView(
                card,
                fullWidth()
        );

        progressText = createTextView(
                "",
                15,
                Color.DKGRAY
        );

        card.addView(
                progressText,
                fullWidth()
        );

        wordText = createTextView(
                "",
                36,
                Color.BLACK
        );

        wordText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        wordText.setGravity(Gravity.CENTER);

        wordText.setPadding(
                0,
                dp(25),
                0,
                dp(18)
        );

        card.addView(
                wordText,
                fullWidth()
        );

        speakButton =
                createButton("🔊 聽英文發音");

        speakButton.setOnClickListener(
                view -> speakCurrentWord()
        );

        card.addView(
                speakButton,
                fullWidth()
        );

        TextView instructionText =
                createTextView(
                        "請選擇正確的中文意思",
                        18,
                        Color.DKGRAY
                );

        instructionText.setGravity(
                Gravity.CENTER
        );

        instructionText.setPadding(
                0,
                dp(22),
                0,
                dp(10)
        );

        card.addView(
                instructionText,
                fullWidth()
        );

        answerButtons =
                new Button[OPTION_COUNT];

        for (int i = 0; i < OPTION_COUNT; i++) {

            final int selectedIndex = i;

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
                    view -> checkAnswer(
                            selectedIndex
                    )
            );

            card.addView(
                    button,
                    params
            );

            answerButtons[i] = button;
        }

        resultText = createTextView(
                "",
                18,
                Color.BLACK
        );

        resultText.setGravity(Gravity.CENTER);

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

        scoreText = createTextView(
                "",
                16,
                Color.DKGRAY
        );

        scoreText.setGravity(Gravity.CENTER);

        card.addView(
                scoreText,
                fullWidth()
        );

        bestScoreText = createTextView(
                "",
                15,
                Color.GRAY
        );

        bestScoreText.setGravity(Gravity.CENTER);

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

        nextButton =
                createButton("下一題");

        nextButton.setVisibility(View.GONE);

        nextButton.setOnClickListener(
                view -> nextQuestion()
        );

        card.addView(
                nextButton,
                fullWidth()
        );

        setContentView(scrollView);
    }

    /*
     * 顯示題目
     */
    private void showQuestion() {

        answered = false;

        Vocabulary.Word currentWord =
                quizWords.get(currentQuestion);

        progressText.setText(
                "第 "
                        + (currentQuestion + 1)
                        + " 題，共 "
                        + quizWords.size()
                        + " 題"
        );

        wordText.setText(
                currentWord.getEnglish()
        );

        resultText.setText("");

        scoreText.setText(
                "目前得分：" + score
        );

        int bestScore =
                preferences.getInt(
                        "best_score",
                        0
                );

        bestScoreText.setText(
                "最佳成績："
                        + bestScore
                        + " / "
                        + quizWords.size()
        );

        generateChoices(currentWord);

        for (int i = 0; i < OPTION_COUNT; i++) {

            Button button =
                    answerButtons[i];

            button.setVisibility(View.VISIBLE);
            button.setEnabled(true);
            button.setText(currentChoices[i]);
            button.setTextColor(Color.BLACK);

            button.setBackgroundColor(
                    Color.rgb(
                            235,
                            232,
                            250
                    )
            );
        }

        speakButton.setVisibility(View.VISIBLE);

        nextButton.setVisibility(View.GONE);

        nextButton.setOnClickListener(
                view -> nextQuestion()
        );
    }

    /*
     * 隨機產生四個中文選項
     */
    private void generateChoices(
            Vocabulary.Word correctWord
    ) {

        List<String> choices =
                new ArrayList<>();

        choices.add(
                correctWord.getChinese()
        );

        while (choices.size() < OPTION_COUNT) {

            int index = random.nextInt(
                    Vocabulary.WORDS.length
            );

            String randomChinese =
                    Vocabulary.WORDS[index]
                            .getChinese();

            if (!choices.contains(randomChinese)) {
                choices.add(randomChinese);
            }
        }

        Collections.shuffle(choices);

        for (int i = 0; i < OPTION_COUNT; i++) {

            currentChoices[i] =
                    choices.get(i);

            if (currentChoices[i].equals(
                    correctWord.getChinese()
            )) {
                correctAnswerIndex = i;
            }
        }
    }

    /*
     * 檢查答案
     */
    private void checkAnswer(
            int selectedIndex
    ) {

        if (answered) {
            return;
        }

        answered = true;

        for (Button button : answerButtons) {
            button.setEnabled(false);
        }

        Button correctButton =
                answerButtons[
                        correctAnswerIndex
                        ];

        correctButton.setBackgroundColor(
                Color.rgb(204, 240, 210)
        );

        correctButton.setTextColor(
                Color.rgb(0, 120, 60)
        );

        if (selectedIndex ==
                correctAnswerIndex) {

            score++;

            resultText.setText(
                    "✓ 答對了！"
            );

            resultText.setTextColor(
                    Color.rgb(0, 120, 60)
            );

        } else {

            Button wrongButton =
                    answerButtons[
                            selectedIndex
                            ];

            wrongButton.setBackgroundColor(
                    Color.rgb(255, 220, 220)
            );

            wrongButton.setTextColor(Color.RED);

            resultText.setText(
                    "✗ 答錯了\n正確答案："
                            + currentChoices[
                            correctAnswerIndex
                            ]
            );

            resultText.setTextColor(Color.RED);
        }

        scoreText.setText(
                "目前得分：" + score
        );

        if (currentQuestion ==
                quizWords.size() - 1) {

            nextButton.setText("查看成績");

        } else {

            nextButton.setText("下一題");
        }

        nextButton.setVisibility(View.VISIBLE);
    }

    /*
     * 下一題
     */
    private void nextQuestion() {

        currentQuestion++;

        if (currentQuestion >=
                quizWords.size()) {

            showFinalResult();

        } else {

            showQuestion();
        }
    }

    /*
     * 顯示最終成績
     */
    private void showFinalResult() {

        int bestScore =
                preferences.getInt(
                        "best_score",
                        0
                );

        if (score > bestScore) {

            bestScore = score;

            preferences.edit()
                    .putInt(
                            "best_score",
                            score
                    )
                    .apply();
        }

        progressText.setText(
                "測驗完成"
        );

        wordText.setText(
                score
                        + " / "
                        + quizWords.size()
        );

        if (score ==
                quizWords.size()) {

            resultText.setText(
                    "太棒了，全部答對！"
            );

        } else if (score >= 8) {

            resultText.setText(
                    "表現很好，繼續保持！"
            );

        } else if (score >= 6) {

            resultText.setText(
                    "表現不錯，再接再厲！"
            );

        } else {

            resultText.setText(
                    "再練習一次，一定會進步！"
            );
        }

        resultText.setTextColor(
                Color.rgb(91, 63, 214)
        );

        scoreText.setText(
                "最佳成績："
                        + bestScore
                        + " / "
                        + quizWords.size()
        );

        bestScoreText.setText("");

        speakButton.setVisibility(View.GONE);

        for (Button button : answerButtons) {
            button.setVisibility(View.GONE);
        }

        nextButton.setText("重新隨機出題");

        nextButton.setVisibility(View.VISIBLE);

        nextButton.setOnClickListener(
                view -> restartQuiz()
        );
    }

    /*
     * 重新隨機測驗
     */
    private void restartQuiz() {

        currentQuestion = 0;
        score = 0;
        answered = false;

        prepareQuiz();
        showQuestion();
    }

    /*
     * 播放目前單字
     */
    private void speakCurrentWord() {

        if (textToSpeech == null
                || quizWords.isEmpty()) {
            return;
        }

        String word =
                quizWords
                        .get(currentQuestion)
                        .getEnglish();

        textToSpeech.speak(
                word,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "toeic_word"
        );
    }

    @Override
    public void onInit(int status) {

        if (status !=
                TextToSpeech.SUCCESS) {

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

    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }

    private TextView createTextView(
            String text,
            int size,
            int color
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(text);
        textView.setTextSize(size);
        textView.setTextColor(color);

        return textView;
    }

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

    private LinearLayout.LayoutParams fullWidth() {

        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {

        return Math.round(
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
