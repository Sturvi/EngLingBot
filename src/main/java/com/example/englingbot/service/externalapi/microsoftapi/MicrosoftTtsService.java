package com.example.englingbot.service.externalapi.microsoftapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        log.debug("Getting access token...");
        return webClient.post()
                .uri(tokenEndpoint)
                .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                .retrieve()
                .bodyToMono(String.class);
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
