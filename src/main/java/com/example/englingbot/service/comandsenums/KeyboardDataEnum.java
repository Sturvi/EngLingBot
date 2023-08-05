package com.example.englingbot.service.comandsenums;

public enum KeyboardDataEnum {

    TRANSLATOR ("get from translator", "Получить онлайн перевод"),
    LEARNED ("learned", "\uD83D\uDC68\u200D\uD83C\uDF93 Уже выучил слово"),
    USAGEEXAMPLES ("usage examples", "Примеры использования"),
    REMEMBERED ("Remembered", "✅ Вспомнил"),
    CONTEXT ("context", "Контекст"),
    NOTREMEMBERED ("not remembered", "⛔️ Не вспомнил"),
    NEXT ("next", "➡\uFE0F Следующее слово"),
    YES ("YES", "✅"),
    NO ("NO", "⛔️"),;


    private final String data;
    private final String text;

    KeyboardDataEnum(String data, String text) {
        this.data = data;
        this.text = text;
    }

    public String getData(){
        return data;
    }

    public String getText() {
        return text;
    }

    public static KeyboardDataEnum fromData(String data) {
        for (KeyboardDataEnum b : KeyboardDataEnum.values()) {
            if (data.startsWith(b.getData())) {
                return b;
            }
        }
        return null;
    }


    public static String getWord(String data) {
        for (KeyboardDataEnum b : KeyboardDataEnum.values()) {
            if (data.startsWith(b.getData())) {
                return data.replaceAll(b.getData(), "").trim();
            }
        }
        return null;
    }
}
