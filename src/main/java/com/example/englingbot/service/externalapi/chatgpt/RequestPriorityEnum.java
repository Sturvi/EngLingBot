package com.example.englingbot.service.externalapi.chatgpt;

import lombok.Getter;

public enum RequestPriorityEnum {
    TRANSLATION(1),
    TRANSCRIPTION(5),
    PRIORITYTRANSCRIPTION(1),
    CONTEXT (10),
    PRIORITYCONTEXT(1),
    USAGEEXAMPLES(10),
    PRIORITYUSAGEEXAMPLES(1);

    @Getter
    private final int priority;

    RequestPriorityEnum(int priority) {
        this.priority = priority;
    }
}
