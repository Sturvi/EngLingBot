package com.example.englingbot.service.comandsenums;

import java.util.Arrays;

public enum KeyboardDataEnum {

    TRANSLATOR ("tr ", "Получить онлайн перевод"),
    LEARNED ("learned ", "\uD83D\uDC68\u200D\uD83C\uDF93 Уже выучил слово"),
    USAGE_EXAMPLES("usage examples ", "Примеры использования"),
    REMEMBERED ("Remembered ", "✅ Вспомнил"),
    CONTEXT ("context ", "Контекст"),
    NOT_REMEMBERED("not remembered ", "⛔️ Не вспомнил"),
    NEXT ("next ", "➡ Следующее слово"),
    YES ("YES ", "✅"),
    NO ("NO ", "⛔️"),
    RE_REVIEW ("Re-review", "Заново проверить в GPT");

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
        return Arrays.stream(KeyboardDataEnum.values())
                .filter(b -> data.toLowerCase().startsWith(b.getData().toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public static String getWord(String data) {
        return Arrays.stream(KeyboardDataEnum.values())
                .filter(b -> data.toLowerCase().startsWith(b.getData().toLowerCase()))
                .map(b -> data.replaceAll(b.getData(), "").trim())
                .findFirst()
                .orElse(null);
    }

    public static Long getWordId(String data) {
        return Arrays.stream(KeyboardDataEnum.values())
                .filter(b -> data.toLowerCase().startsWith(b.getData().toLowerCase()))
                .map(b -> data.replaceAll(b.getData(), "").trim())
                .findFirst()
                .map(Long::parseLong)
                .orElse(null);
    }
}
