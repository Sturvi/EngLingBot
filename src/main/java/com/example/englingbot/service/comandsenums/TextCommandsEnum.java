package com.example.englingbot.service.comandsenums;

public enum TextCommandsEnum {
    START("/start"),
    HELP("/help"),
    ANSWER("/answer"),
    ADD_WORD("\uD83D\uDDD2 Добавить слова"),
    LEARN_WORD("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDF93 Учить слова"),
    REPEAT_WORD("\uD83D\uDD01 Повторять слова"),
    MIXED_MODE("\uD83D\uDD00 Смешанный режим"),
    LIST_STUDY_WORDS("\uD83D\uDCD3 Список изучаемых слов"),
    LIST_REPETITION_WORDS("\uD83D\uDCD3 Список слов на повторении"),
    ADD_RANDOM_WORDS("\uD83D\uDCD6 Добавить случайные слова"),
    STATISTIC("/statistic"),
    DELETE("/delete"),
    ;

    private final String command;

    TextCommandsEnum(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static TextCommandsEnum fromString(String textCommand) {
        for (TextCommandsEnum b : TextCommandsEnum.values()) {
            if (b.command.equalsIgnoreCase(textCommand)) {
                return b;
            }
        }
        return null;
    }
}
