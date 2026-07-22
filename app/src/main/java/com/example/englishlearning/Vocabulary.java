package com.example.englishlearning;

public final class Vocabulary {

    private Vocabulary() {
        // 防止建立物件
    }

    public static final Word[] WORDS = {

            new Word("abandon", "拋棄；遺棄"),
            new Word("abide", "忍受；容忍"),
            new Word("ability", "能力；才能"),
            new Word("able", "能；可；會"),
            new Word("abnormal", "不正常的；反常的"),
            new Word("abroad", "在國外；到國外"),
            new Word("aboard", "在交通工具上"),
            new Word("abolish", "廢除；廢止"),
            new Word("aborigine", "土著；原住民"),
            new Word("abort", "中止；使失敗"),
            new Word("abound", "充滿；富足"),
            new Word("about", "關於；大約"),
            new Word("above", "在上面；超過"),
            new Word("above-average", "高於平均的"),
            new Word("abrupt", "突然的；唐突的"),
            new Word("abruptly", "突然地；唐突地"),
            new Word("absence", "缺席；不在"),
            new Word("absent", "缺席的；不在場的"),
            new Word("absolute", "完全的；絕對的"),
            new Word("absolutely", "完全地；絕對地"),
            new Word("absorb", "吸收；理解"),
            new Word("abstract", "抽象的；摘要"),
            new Word("absurd", "荒謬的；不合理的"),
            new Word("abundance", "豐富；充足"),
            new Word("abundant", "大量的；充足的"),
            new Word("abuse", "濫用；虐待"),
            new Word("abusive", "辱罵的；濫用的"),
            new Word("academic", "學術的；學校的"),
            new Word("academy", "學院；大學"),
            new Word("accelerate", "加快；加速"),
            new Word("accent", "口音；強調"),
            new Word("accept", "接受；答應"),
            new Word("acceptable", "可以接受的；令人滿意的"),
            new Word("acceptance", "接受；贊同"),
            new Word("access", "進入；使用"),
            new Word("accessible", "可接近的；可進入的"),
            new Word("accessory", "附件；配件"),
            new Word("accident", "事故；意外"),
            new Word("accidental", "偶然的；意外的"),
            new Word("accomplish", "完成；實現")
    };

    public static final class Word {

        private final String english;
        private final String chinese;

        public Word(String english, String chinese) {
            this.english = english;
            this.chinese = chinese;
        }

        public String getEnglish() {
            return english;
        }

        public String getChinese() {
            return chinese;
        }
    }
}
