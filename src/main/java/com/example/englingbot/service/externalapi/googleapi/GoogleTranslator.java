package com.example.englingbot.service.externalapi.googleapi;

import com.example.englingbot.model.dto.WordDto;
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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * A service class to interact with the Google Translate API.
 */
@Slf4j
@Service
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


    public WordDto translate(String word) {
        WordDto wordDto = new WordDto();
        String[] languages = {"ru", "en"};
        for (String language : languages) {
            log.info("Translating word {} to language {}", word, language);
            HttpEntity<Map<String, String>> entity = createRequestEntity(word, language);
            Mono<String> response = sendHttpRequest(entity);
            parseResponseToWordDto(response.block(), wordDto, language);
            log.info("Translation completed for language {}", language);
        }

        return wordDto;
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

    private void parseResponseToWordDto(String response, WordDto wordDto, String language) {
        JsonObject root = gson.fromJson(response, JsonObject.class);
        JsonArray translations = root.getAsJsonObject("data").getAsJsonArray("translations");

        for (JsonElement translation : translations) {
            String translatedText = translation.getAsJsonObject().get("translatedText").getAsString();

            if (language.equals("ru")) {
                wordDto.setRussianWord(translatedText);
            } else if (language.equals("en")) {
                wordDto.setEnglishWord(translatedText);
            }
        }
    }
}
