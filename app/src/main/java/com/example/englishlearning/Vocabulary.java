package com.example.englishlearning;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Vocabulary {

    private static final String ASSET_FILE = "toeic_words.tsv";
    private static List<Word> cachedWords;

    private Vocabulary() {
    }

    public static synchronized List<Word> load(
            Context context
    ) throws IOException {

        if (cachedWords != null) {
            return cachedWords;
        }

        List<Word> words =
                new ArrayList<>(3000);

        try (
                InputStream input =
                        context.getAssets()
                                .open(ASSET_FILE);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        input,
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()
                        || line.startsWith("#")) {
                    continue;
                }

                String[] columns =
                        line.split("\\t", 3);

                if (columns.length != 3) {
                    continue;
                }

                try {
                    int number =
                            Integer.parseInt(
                                    columns[0]
                            );

                    String english =
                            columns[1].trim();

                    String chinese =
                            columns[2].trim();

                    if (!english.isEmpty()
                            && !chinese.isEmpty()) {

                        words.add(
                                new Word(
                                        number,
                                        english,
                                        chinese
                                )
                        );
                    }

                } catch (NumberFormatException ignored) {
                    // 格式錯誤的資料不載入
                }
            }
        }

        if (words.size() != 3000) {

            throw new IOException(
                    "題庫載入不完整：預期 3000 筆，實際 "
                            + words.size()
                            + " 筆"
            );
        }

        cachedWords =
                Collections.unmodifiableList(
                        words
                );

        return cachedWords;
    }

    public static final class Word {

        private final int number;
        private final String english;
        private final String chinese;

        public Word(
                int number,
                String english,
                String chinese
        ) {
            this.number = number;
            this.english = english;
            this.chinese = chinese;
        }

        public int getNumber() {
            return number;
        }

        public String getEnglish() {
            return english;
        }

        public String getChinese() {
            return chinese;
        }
    }
}
