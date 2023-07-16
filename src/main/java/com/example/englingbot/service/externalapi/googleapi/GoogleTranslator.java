package com.example.englingbot.service.externalapi.googleapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class GoogleTranslator {

    @Value("classpath:google-api-key.txt")
    private Resource apiKeyResource;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleTranslator() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public void translate(String word, String language, ArrayList<String> resultList) {
        HttpEntity<Map<String, String>> entity = createRequestEntity(word, language);
        ResponseEntity<String> response = sendHttpRequest(entity);
        parseResponse(response, resultList);
    }

    private HttpEntity<Map<String, String>> createRequestEntity(String word, String language) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<>();
        map.put("q", word);
        map.put("target", language);

        return new HttpEntity<>(map, headers);
    }

    private ResponseEntity<String> sendHttpRequest(HttpEntity<Map<String, String>> entity) {
        String apiKey = getApiKey();

        try {
            return restTemplate.exchange(
                    "https://translation.googleapis.com/language/translate/v2?key=" + apiKey,
                    HttpMethod.POST,
                    entity,
                    String.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения слова из переводчика.", e);
        }
    }

    private String getApiKey() {
        try {
            return FileCopyUtils.copyToString(
                    new InputStreamReader(apiKeyResource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать API ключ.", e);
        }
    }

    private void parseResponse(ResponseEntity<String> response, ArrayList<String> resultList) {
        JsonNode root;

        try {
            root = objectMapper.readTree(response.getBody());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка парсинга ответа от переводчика.", e);
        }

        JsonNode dataNode = root.path("data");
        JsonNode translations = dataNode.path("translations");

        for (JsonNode translation : translations) {
            String translatedText = translation.path("translatedText").asText();
            resultList.add(translatedText);
        }
    }
}

