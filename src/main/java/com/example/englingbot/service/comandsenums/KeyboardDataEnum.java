package com.example.englingbot.service.comandsenums;

public enum KeyboardDataEnum {

    YES ("yes", "✅"),
    NO ("no", "⛔️"),
    TRANSLATOR ("get from translator", "Получить онлайн перевод"),
    LEARNED ("learned", "\uD83D\uDC68\u200D\uD83C\uDF93 Уже выучил слово"),
    USAGEEXAMPLES ("usage examples", "Примеры использования"),
    REMEMBERED ("Remembered", "✅ Вспомнил"),
    CONTEXT ("context", "Контекст"),
    NOTREMEMBERED ("not remembered", "⛔️ Не вспомнил");


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
            if (b.getData().equalsIgnoreCase(data)) {
                return b;
            }
        }
        return null;
    }
}
