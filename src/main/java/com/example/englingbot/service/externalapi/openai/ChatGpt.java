package com.example.englingbot.service.externalapi.openai;

import com.example.englingbot.model.Message;
import com.example.englingbot.service.externalapi.openai.enums.Role;
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

@Slf4j
@Component
public class ChatGpt {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final Gson gson;

    @Autowired
    public ChatGpt(WebClient webClient, Gson gson) {
        this.webClient = webClient;
        this.gson = gson;
    }

    public String chat(String prompt) {
        Message message = Message
                .builder()
                .role(Role.USER)
                .content(prompt)
                .build();

        return chat(List.of(message));
    }

    public String chat(List<Message> messages) {
        log.info("Sending chat to OpenAI ChatGPT API: {}", messages);
        var requestEntity = createRequestEntity(messages);
        Mono<String> response = sendHttpRequest(requestEntity);
        return parseResponse(response.block());
    }

    private Map<String, Object> createRequestEntity(List<Message> messages) {
        log.debug("Creating request entity for chat: {}", messages);

        List<Map<String, String>> serializedMessages = new ArrayList<>();
        for (Message message : messages) {
            serializedMessages.add(Map.of(
                    "role", message.getRole().getJsonValue(),
                    "content", message.getContent()
            ));
        }

        return Map.of("model", "gpt-3.5-turbo", "messages", serializedMessages);
    }


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
