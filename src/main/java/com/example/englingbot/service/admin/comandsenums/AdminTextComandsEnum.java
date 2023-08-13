package com.example.englingbot.service.admin.comandsenums;

import com.example.englingbot.service.comandsenums.TextCommandsEnum;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum AdminTextComandsEnum {
    START("/start"),
    REVIEW_NEW_WORD("Проверять новые слова");

    private final String command;

    AdminTextComandsEnum(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    private static final Map<String, AdminTextComandsEnum> commands =
            Arrays.stream(AdminTextComandsEnum.values())
                    .collect(Collectors.toMap(
                            element -> element.getCommand().toUpperCase(),
                            element -> element
                    ));

    public static AdminTextComandsEnum fromString(String textCommand) {
        return commands.get(textCommand.toUpperCase());
    }
}
