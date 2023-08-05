package com.example.englingbot.service.externalapi.chatgpt;

import lombok.Getter;
import lombok.Setter;

/**
 * Request class represents a request to the ChatGPT API.
 * It includes the prompt, priority, and response attributes.
 */
@Getter
public class Request {

    /**
     * The prompt for the ChatGPT API request.
     */
    private final String promt;

    /**
     * The priority level of the request.
     */
    private final int priority;

    /**
     * The response from the ChatGPT API.
     */
    @Setter
    private volatile String response;

    /**
     * Constructs a new Request with the specified prompt and priority.
     *
     * @param promt    the prompt for the ChatGPT API request.
     * @param priority the priority level of the request.
     */
    public Request(String promt, int priority) {
        this.promt = promt;
        this.priority = priority;
    }
}
