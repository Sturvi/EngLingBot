
package com.example.englingbot.service.externalapi.chatgpt;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Request {

    private final String promt;
    private final int priority;
    @Setter
    private volatile String response;

    public Request(String promt, int priority) {
        this.promt = promt;
        this.priority = priority;
    }
}