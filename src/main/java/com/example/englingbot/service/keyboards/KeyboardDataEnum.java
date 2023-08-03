package com.example.englingbot.service.keyboards;

public enum KeyboardDataEnum {

    YES ("yes", "✅"),
    NO ("no", "⛔️"),
    TRANSLATOR ("get from translator", "Получить онлайн перевод");


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
