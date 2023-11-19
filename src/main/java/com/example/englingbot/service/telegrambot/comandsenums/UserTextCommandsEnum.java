package com.example.englingbot.service.telegrambot.comandsenums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum UserTextCommandsEnum {
    START("/start"),
    HELP("/help"),
    ANSWER("/answer"),
    ADD_WORD("\uD83D\uDDD2 Добавить слова"),
    LEARN_WORD("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDF93 Учить слова"),
    LIST_REPETITION_WORDS("/vocabulary"),
    STATISTIC("/statistic"),
    DELETE("/delete"),
    CHAT_WITH_TUTOR ("\uD83D\uDC69 Чат-бот \"Таня\""),
    NEW_CHAT ("\uD83E\uDDF9 Начать новый чат"),
    HOME ("\uD83C\uDFE0 Домой");

    private final String command;

    UserTextCommandsEnum(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    private static final Map<String, UserTextCommandsEnum> commands =
            Arrays.stream(UserTextCommandsEnum.values())
                    .collect(Collectors.toMap(
                            element -> element.getCommand().toUpperCase(),
                            element -> element
                    ));

    public static UserTextCommandsEnum fromString(String textCommand) {
        System.out.println(textCommand);
        System.out.println(commands);
        return commands.get(textCommand.toUpperCase());
    }
}
