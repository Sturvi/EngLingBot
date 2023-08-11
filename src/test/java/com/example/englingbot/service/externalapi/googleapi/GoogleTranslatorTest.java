package com.example.englingbot.service.externalapi.googleapi;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleTranslatorTest {

    private String apiKey = "your-api-key";

    @Spy
    private Gson gson;

    @Mock
    private WebClient webClient;

    @Spy
    @InjectMocks
    private GoogleTranslator googleTranslator;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Устанавливаем значение apiKey в googleTranslator используя рефлексию
        Field apiKeyField = GoogleTranslator.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        apiKeyField.set(googleTranslator, apiKey);


        // Устанавливаем значение webClient в googleTranslator используя рефлексию
        Field webClientField = GoogleTranslator.class.getDeclaredField("webClient");
        webClientField.setAccessible(true);
        webClientField.set(googleTranslator, webClient);
    }

    @Test
    public void translateTest() {
        Mono<String> response = mock(Mono.class);

        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(BodyInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(response);

        String responseStringEn = "{\n" +
                "    \"data\": {\n" +
                "        \"translations\": [\n" +
                "            {\n" +
                "                \"translatedText\": \"car\",\n" +
                "                \"detectedSourceLanguage\": \"ru\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}\n";

        String responseStringRu = "{\n" +
                "    \"data\": {\n" +
                "        \"translations\": [\n" +
                "            {\n" +
                "                \"translatedText\": \"машина\",\n" +
                "                \"detectedSourceLanguage\": \"ru\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}\n";

        when(response.block()).thenReturn(responseStringRu, responseStringEn);

        var result = googleTranslator.translate("машина");

/*        assertEquals("машина", result.get("ru"));
        assertEquals("car", result.get("en"));*/
    }

}
