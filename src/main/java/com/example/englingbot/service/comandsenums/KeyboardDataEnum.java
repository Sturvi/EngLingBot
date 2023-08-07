package com.example.englingbot.service.comandsenums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum KeyboardDataEnum {

    TRANSLATOR ("get from translator", "Получить онлайн перевод"),
    LEARNED ("learned", "\\uD83D\\uDC68\\u200D\\uD83C\\uDF93 Уже выучил слово"),
    USAGEEXAMPLES ("usage examples", "Примеры использования"),
    REMEMBERED ("Remembered", "✅ Вспомнил"),
    CONTEXT ("context", "Контекст"),
    NOTREMEMBERED ("not remembered", "⛔️ Не вспомнил"),
    NEXT ("next", "➡ Следующее слово"),
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
}
