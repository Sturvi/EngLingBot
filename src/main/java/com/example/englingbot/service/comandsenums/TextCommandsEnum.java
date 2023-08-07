package com.example.englingbot.service.comandsenums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
    DELETE("/delete");

    private final String command;

    TextCommandsEnum(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    private static final Map<String, TextCommandsEnum> commands =
            Arrays.stream(TextCommandsEnum.values())
                    .collect(Collectors.toMap(
                            element -> element.getCommand().toUpperCase(),
                            element -> element
                    ));

    public static TextCommandsEnum fromString(String textCommand) {
        System.out.println(textCommand);
        System.out.println(commands);
        return commands.get(textCommand.toUpperCase());
    }
}
