package com.example.englingbot.service.externalapi.chatgpt;

import com.google.common.util.concurrent.RateLimiter;
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

import java.util.*;

/**
 * ChatGpt class for interacting with OpenAI ChatGPT API.
 */
@Slf4j
@Component
public class ChatGpt {

    @Value("${openai.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private final Gson gson;


    /**
     * Constructs a ChatGpt instance.
     *
     * @param webClient WebClient used for HTTP requests
     * @param gson      Gson instance for JSON parsing
     */
    @Autowired
    public ChatGpt(WebClient webClient, Gson gson) {
        this.webClient = webClient;
        this.gson = gson;
    }

    /**
     * Sends chat prompt to OpenAI API and returns the response.
     *
     * @param promt The prompt to be sent
     * @return The response from the OpenAI API
     */
    public String chat(String promt) {
        log.info("Sending word {} to OpenAI ChatGPT API", promt);
        var requestEntity = createRequestEntity(promt);
        Mono<String> response = sendHttpRequest(requestEntity);
        return parseResponse(response.block());
    }

    /**
     * Creates a request entity for sending to the OpenAI API.
     *
     * @param promt The prompt to be sent
     * @return A map representing the request entity
     */
    private Map<String, Object> createRequestEntity(String promt) {
        log.debug("Creating request entity for promt: {}", promt);
        Map<String, String> messageContent = Map.of("role", "system", "content", promt);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(messageContent);

        return Map.of("model", "gpt-3.5-turbo", "messages", messages);
    }

    /**
     * Sends an HTTP request with the given entity and returns the response.
     *
     * @param entity The entity to be sent in the request
     * @return A Mono representing the response
     */
    private Mono<String> sendHttpRequest(Map<String, Object> entity) {
        try {
            log.debug("Start sendHttpRequest method for {}", entity);
            return webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .body(BodyInserters.fromValue(entity))
                    .exchange()
                    .flatMap(response -> {
                        log.info("Response HTTP Status: {}", response.statusCode());
                        if (response.statusCode().isError()) {
                            return response.bodyToMono(String.class)
                                    .doOnNext(errorBody -> log.error("Error Body: {}", errorBody))
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("Error retrieving word from ChatGPT. Status: " + response.statusCode() + " Error Body: " + errorBody)));
                        } else {
                            return response.bodyToMono(String.class);
                        }
                    });
        } catch (Exception e) {
            log.error("Error retrieving word from ChatGPT.", e);
            throw new RuntimeException("Error retrieving word from ChatGPT.", e);
        }
    }

    /**
     * Parses the response from the OpenAI API.
     *
     * @param response The response to be parsed
     * @return The parsed response
     */
    private String parseResponse(String response) {
        log.debug("Parsing response: {}", response);
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
