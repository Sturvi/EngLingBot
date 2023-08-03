package com.example.englingbot.service.externalapi.chatgpt;

import lombok.Getter;

public enum RequestPriorityEnum {
    TRANSLATION(1),
    TRANSCRIPTION(5),
    CONTEXT (10),
    USAGEEXAMPLES(10);

    @Getter
    private final int priority;

    RequestPriorityEnum(int priority) {
        this.priority = priority;
    }
}
