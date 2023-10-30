package com.example.englingbot.service.externalapi.microsoftapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
public class MicrosoftTtsService {
    @Value("${microsoft.tts.subscriptionKey}")
    private String subscriptionKey;

    @Value("${microsoft.tts.serviceRegion}")
    private String serviceRegion;

    @Value("${microsoft.tts.voiceName}")
    private String voiceName;

    private final WebClient webClient;
    private String cachedToken;
    private Instant tokenExpiryTime;

    @Autowired
    public MicrosoftTtsService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void textToSpeech(String text, Path outputFilePath) {
        log.debug("Converting text to speech...");
        byte[] audioData = getAudioData(text).block();
        saveAudioDataToFile(audioData, outputFilePath);
        log.debug("Text to speech conversion completed.");
    }

    public File textToSpeechFile(String text) {
        log.debug("Converting text to speech and returning as File...");

        byte[] audioData  = getAudioData(text).block();

        File tempFile;
        try {
            // Create a temporary file
            tempFile = File.createTempFile("audio", ".wav");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(audioData);
            }
        } catch (IOException e) {
            log.error("Failed to write to temp file.", e);
            throw new RuntimeException("Failed to write to temp file.", e);
        }

        log.debug("Text to speech conversion to File completed.");
        return tempFile;
    }

    private Mono<byte[]> getAudioData(String text) {
        String tokenEndpoint = getTokenEndpoint();
        String ttsEndpoint = getTtsEndpoint();
        return getAccessToken(tokenEndpoint)
                .flatMap(token -> createRequest(token, ttsEndpoint, text)
                        .retrieve()
                        .bodyToMono(byte[].class)
                );
    }

    private WebClient.RequestHeadersSpec<?> createRequest(String token, String ttsEndpoint, String text) {
        log.debug("Creating request to Microsoft Text to Speech API...");
        return webClient.post()
                .uri(ttsEndpoint)
                .header("Authorization", "Bearer " + token)
                .header("X-Microsoft-OutputFormat", "riff-24khz-16bit-mono-pcm")
                .header("Content-Type", "application/ssml+xml")
                .body(BodyInserters.fromValue(getSsml(text)));
    }

    private Mono<String> getAccessToken(String tokenEndpoint) {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiryTime)) {
            return Mono.just(cachedToken);
        }

        return webClient.post()
                .uri(tokenEndpoint)
                .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                .header("Content-Length", "0")
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(token -> {
                    // Декодирование токена
                    String[] splitToken = token.split("\\.");
                    String payload = new String(Base64.getDecoder().decode(splitToken[1]));

                    // Извлечение поля exp (время истечения)
                    try {
                        JsonObject payloadJson = JsonParser.parseString(payload).getAsJsonObject();
                        long expTimestamp = payloadJson.get("exp").getAsLong();

                        // Установка времени истечения и кеширование токена
                        tokenExpiryTime = Instant.ofEpochSecond(expTimestamp);
                        cachedToken = token;
                    } catch (Exception e) {
                        tokenExpiryTime = Instant.now().plus(Duration.ofMinutes(9));
                        cachedToken = token;
                        log.error("Failed to parse token payload.", e);
                    }
                })
                .doOnError(e -> log.error("Error occurred while retrieving access token.", e));
    }


    private String getSsml(String text) {
        return "<speak version='1.0' xml:lang='en-US'><voice xml:lang='en-US' xml:gender='Female' name='" + voiceName + "'>" + text + "</voice></speak>";
    }

    private String getTokenEndpoint() {
        return "https://" + serviceRegion + ".api.cognitive.microsoft.com/sts/v1.0/issueToken";
    }

    private String getTtsEndpoint() {
        return "https://" + serviceRegion + ".tts.speech.microsoft.com/cognitiveservices/v1";
    }

    private void saveAudioDataToFile(byte[] audioData, Path outputFilePath) {
        try {
            log.info("Saving audio data to file...");
            Files.write(outputFilePath, audioData);
        } catch (IOException e) {
            log.error("Failed to save audio file.", e);
            throw new RuntimeException("Failed to save audio file.", e);
        }
    }
}
