package com.example.englingbot.service.externalapi.googleapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * A service class to interact with the Google Translate API.
 */
@Slf4j
@Component
public class GoogleTranslator {

    @Value("${google.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final Gson gson;

    @Autowired
    public GoogleTranslator(WebClient webClient, Gson gson) {
        this.webClient = webClient;
        this.gson = gson;
    }


    /**
     * Translates the given word to Russian and English, then stores and returns the results in a Map.
     * To retrieve the Russian translation from the map, use the key "ru". For the English translation, use the key "en".
     *
     * @param word       The word to be translated.
     * @return           A map containing the translations of the word in Russian and English.
     */
    public Map<String, String> translate(String word) {
        var resultMap = new HashMap <String, String>();
        String[] languages = {"ru", "en"};
        for (String language : languages) {
            log.info("Translating word {} to language {}", word, language);
            HttpEntity<Map<String, String>> entity = createRequestEntity(word, language);
            Mono<String> response = sendHttpRequest(entity);
            parseResponse(response.block(), resultMap, language);
            log.info("Translation completed for language {}", language);
        }

        return resultMap;
    }

    /**
     * Creates HTTP entity for the translation request.
     * @param word The word to be translated.
     * @param language The target language code.
     * @return The HTTP entity containing the request data.
     */
    private HttpEntity<Map<String, String>> createRequestEntity(String word, String language) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = Map.of("q", word, "target", language);

        return new HttpEntity<>(map, headers);
    }

    /**
     * Sends an HTTP request to Google Translate API.
     * @param entity The HTTP entity containing the request data.
     * @return The response from Google Translate API as a String.
     */
    private Mono<String> sendHttpRequest(HttpEntity<Map<String, String>> entity) {
        try {
            return webClient.post()
                    .uri("https://translation.googleapis.com/language/translate/v2?key=" + apiKey)
                    .body(BodyInserters.fromValue(entity.getBody()))
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (Exception e) {
            log.error("Error retrieving word from translator.", e);
            throw new RuntimeException("Error retrieving word from translator.", e);
        }
    }

    /**
     * Parses the response from Google Translate API and puts the translations into the provided resultMap.
     * @param response The response from Google Translate API as a String.
     * @param resultMap The map where the translations will be stored with language codes as keys.
     * @param language The language code of the translation.
     */
    private void parseResponse(String response, Map<String, String> resultMap, String language) {
        JsonObject root = gson.fromJson(response, JsonObject.class);
        JsonArray translations = root.getAsJsonObject("data").getAsJsonArray("translations");

        for (JsonElement translation : translations) {
            String translatedText = translation.getAsJsonObject().get("translatedText").getAsString();
            resultMap.put(language, translatedText);
        }
    }
}
