package com.example.englingbot.service.externalapi.openai.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    USER("user"),
    SYSTEM("system"),
    ASSISTANT("assistant");

    private final String jsonValue;

    Role(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }
}
