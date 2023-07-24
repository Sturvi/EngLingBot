package com.example.englingbot.service.externalapi.chatgpt;

import com.example.englingbot.model.Word;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ChatGptWordTranslator extends ChatGpt{
    private static final Pattern pattern = Pattern.compile("(ru:\\s?[а-яёА-ЯЁ]+,\\s?en:\\s?[a-zA-Z]+\\.\\s?)+");

    public ChatGptWordTranslator(WebClient webClient, Gson gson) {
        super(webClient, gson);
    }

    public List<Word> getTranslatedWordList (String incomingWord) {
        String prompt = createPrompt(incomingWord, ChatGptPromtsEnum.NEWWORD);
        var apiResponse = chat(prompt);
        log.debug("Получен ответ из Chat Gpt: {}", apiResponse);

        List<Word> words = new ArrayList<>();

        if (isMatchingPattern(apiResponse)) {
            log.info("The string matches the pattern");
            String[] splitApiResponse = splitApiResponse(apiResponse);

            for (String str : splitApiResponse) {
                Word word = createWordFromStr(str);
                words.add(word);
                log.debug("Russian word: {}, English word: {}", word.getRussianWord(), word.getEnglishWord());
            }

        } else {
            log.warn("The string does not match the pattern");
        }

        return words;
    }

    public String getTranscription(String incomingWord) {
        log.info("Getting transcription for: {}", incomingWord);

        String prompt = createPrompt(incomingWord, ChatGptPromtsEnum.TRANSCRIPTIONS);
        log.debug("Created prompt: {}", prompt);

        String response = chat(prompt);
        log.debug("Received response: {}", response);

        if (!response.matches("^\\[.*\\]$")) {
            log.debug("Response matches regular expression, returning null.");
            return null;
        }

        log.info("Returning transcription: {}", response);
        return response;
    }

    private String createPrompt(String incomingWord, ChatGptPromtsEnum chatGptPromtsEnum) {
        return chatGptPromtsEnum
                .getPrompt()
                .formatted(incomingWord);
    }

    private boolean isMatchingPattern(String apiResponse) {
        Matcher matcher = pattern.matcher(apiResponse);
        return matcher.matches();
    }

    private String[] splitApiResponse(String apiResponse) {
        return apiResponse.split("\\. ");
    }

    private Word createWordFromStr(String str) {
        String[] splitStr = str.split(", ");

        String russianWord = splitStr[0].substring(4); // 4 - длина строки "ru: "
        String englishWord = splitStr[1].substring(4); // 4 - длина строки "en: "

        return Word.builder()
                .russianWord(russianWord)
                .englishWord(englishWord)
                .build();
    }
}
