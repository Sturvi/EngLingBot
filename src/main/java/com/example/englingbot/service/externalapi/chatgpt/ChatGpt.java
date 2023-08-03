
package com.example.englingbot.service.externalapi.chatgpt;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
import java.util.PriorityQueue;

@Slf4j
@Component
public class ChatGpt {

    @Value("${openai.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private final Gson gson;
    private final RateLimiter rateLimiter = RateLimiter.create(0.047);

    private PriorityQueue<Request> requestQueue = new PriorityQueue<>((r1, r2) -> Integer.compare(r1.getPriority(), r2.getPriority()));
    private Thread processRequestThread;
    private volatile boolean running = false;

    @Autowired
    public ChatGpt(WebClient webClient, Gson gson) {
        this.webClient = webClient;
        this.gson = gson;
    }

    @PostConstruct
    private void startProcessRequests() {
        Runnable runnable = this::processRequests;

        running = true;
        processRequestThread = new Thread(runnable);
        processRequestThread.start();
    }

    @PreDestroy
    private void stopProcessRequests() {
        running = false;
        if (processRequestThread != null) {
            processRequestThread.interrupt();
        }
    }

    public void addRequest(Request request) {
        synchronized (requestQueue) {
            requestQueue.add(request);
            requestQueue.notify();
        }
    }

    private String chat(String promt) {
        log.info("Sending word {} to OpenAI ChatGPT API", promt);
        var requestEntity = createRequestEntity(promt);
        Mono<String> response = sendHttpRequest(requestEntity);
        return parseResponse(response.block());
    }

    private Map<String, Object> createRequestEntity(String promt) {
        Map<String, String> messageContent = Map.of("role", "system", "content", promt);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(messageContent);

        return Map.of("model", "gpt-3.5-turbo", "messages", messages);
    }

    private Mono<String> sendHttpRequest(Map<String, Object> entity) {
        try {
            log.debug("Start sendHttpRequest method for " + entity);
            return webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .body(BodyInserters.fromValue(entity))
                    .exchange()
                    .flatMap(response -> {
                        log.info("Response HTTP Status: " + response.statusCode());
                        if (response.statusCode().isError()) {
                            return response.bodyToMono(String.class)
                                    .doOnNext(errorBody -> log.error("Error Body: " + errorBody))
                                    .flatMap(errorBody -> Mono.<String>error(new RuntimeException("Error retrieving word from ChatGPT. Status: " + response.statusCode() + " Error Body: " + errorBody)));
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

    public void processRequests() {
        while (running) {
            Request request = null;
            rateLimiter.acquire();
            synchronized (requestQueue) {
                while (requestQueue.isEmpty() && running) {
                    try {
                        requestQueue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (running) {
                    request = requestQueue.poll();
                }
            }

            if (request != null && running) {
                try {
                    String response = chat(request.getPromt());
                    request.setResponse(response);
                } catch (Exception e) {
                    request.setResponse("An error occurred while sending the request.");
                    log.error("Error processing request", e);
                }
            }
        }
    }
}