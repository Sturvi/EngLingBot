package com.example.englingbot.service.externalapi.chatgpt;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class provides an interface to the OpenAI ChatGPT API.
 */
@Slf4j
@Component
public class ChatGpt {

    /**
     * OpenAI API key
     */
    @Value("${openai.api.key}")
    private String apiKey;

    /**
     * WebClient is a non-blocking, reactive client to perform HTTP requests.
     */
    private final WebClient webClient;

    /**
     * Gson is a Java library that can be used to convert Java Objects into their JSON representation.
     */
    private final Gson gson;

    /**
     * Constructs a new instance of ChatGpt
     *
     * @param webClient WebClient instance
     * @param gson Gson instance
     */
    @Autowired
    public ChatGpt(WebClient webClient, Gson gson) {
        this.webClient = webClient;
        this.gson = gson;
    }

    /**
     * Method to start a chat session with ChatGPT API using a provided prompt.
     *
     * @param promt The initial text input for the chat.
     * @return The response from the ChatGPT API.
     */
    public String chat(String promt) {
        log.info("Sending word {} to OpenAI ChatGPT API", promt);
        var requestEntity = createRequestEntity(promt);
        Mono<String> response = sendHttpRequest(requestEntity);
        return parseResponse(response.block());
    }

    /**
     * Creates the request entity for the chat session.
     *
     * @param promt The initial text input for the chat.
     * @return A map containing the request entity.
     */
    private Map<String, Object> createRequestEntity(String promt) {
        Map<String, String> messageContent = Map.of("role", "system", "content", promt);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(messageContent);

        return Map.of("model", "gpt-3.5-turbo", "messages", messages);
    }

    /**
     * Sends the HTTP request to the OpenAI ChatGPT API.
     *
     * @param entity The request entity for the chat session.
     * @return The response from the API as a Mono.
     */
    private Mono<String> sendHttpRequest(Map<String, Object> entity) {
        try {
            return webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .body(BodyInserters.fromValue(entity))
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (Exception e) {
            log.error("Error retrieving word from ChatGPT.", e);
            throw new RuntimeException("Error retrieving word from ChatGPT.", e);
        }
    }

    /**
     * Parses the response from the OpenAI ChatGPT API.
     *
     * @param response The raw JSON response from the API.
     * @return The message content from the API response as a string, or null if the response contains no choices.
     */
    private String parseResponse(String response) {
        JsonObject root = gson.fromJson(response, JsonObject.class);
        JsonArray choices = root.getAsJsonArray("choices");

        if (choices.size() > 0) {
            return choices.get(0)
                    .getAsJsonObject()
                    .get("message")
                    .getAsJsonObject()
                    .get("content")
                    .getAsString();
        }

        return null;
    }
}
