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


/**
 * ChatGptWordUtils is a utility class for working with the ChatGPT API.
 * It includes methods for fetching translations, transcriptions, usage examples, and word context.
 * The class extends the base class ChatGpt.
 */
@Component
@Slf4j
public class ChatGptWordUtils extends ChatGpt{
    private static final Pattern pattern = Pattern.compile("(ru:\\s?[а-яёА-ЯЁ]+,\\s?en:\\s?[a-zA-Z]+\\.\\s?)+");

    /**
     * Constructor for the ChatGptWordUtils class.
     *
     * @param webClient WebClient instance for making HTTP requests.
     * @param gson Gson instance for parsing JSON.
     */
    public ChatGptWordUtils(WebClient webClient, Gson gson) {
        super(webClient, gson);
    }

    /**
     * Fetches the translations for the provided word using the ChatGPT API.
     *
     * @param incomingWord the word to be translated.
     * @return a list of Word objects containing the translations.
     */
    public List<Word> getTranslations (String incomingWord) {
        log.debug("Entering getTranslations (String incomingWord)");

        String prompt = constructPrompt(incomingWord, ChatGptPromptsEnum.NEWWORD);
        var apiResponse = chat(prompt);
        log.debug("Получен ответ из Chat Gpt: {}", apiResponse);

        List<Word> words = new ArrayList<>();

        if (doesMatchPattern(apiResponse)) {
            log.info("The string matches the pattern");
            String[] splitResponse = splitResponse(apiResponse);

            for (String str : splitResponse) {
                Word word = extractWordFromResponse(str);
                words.add(word);
                log.debug("Russian word: {}, English word: {}", word.getRussianWord(), word.getEnglishWord());
            }
        } else {
            log.warn("The string does not match the pattern");
        }

        log.debug("Returning words: {}", words);
        return words;
    }

    /**
     * Fetches the transcription for the provided word using the ChatGPT API.
     *
     * @param word the word for which to fetch the transcription.
     * @return a string containing the transcription of the word.
     */
    public String fetchTranscription(String word) {
        log.debug("Entering fetchTranscription(String word)");

        log.info("Getting transcription for: {}", word);

        String prompt = constructPrompt(word, ChatGptPromptsEnum.TRANSCRIPTIONS);
        log.debug("Created prompt: {}", prompt);

        String response = chat(prompt);
        log.debug("Received response: {}", response);

        if (!response.matches("^\\[.*\\]$")) {
            log.debug("Response matches regular expression, returning null.");
            return null;
        }

        log.info("Returning transcription: {}", response);
        log.debug("Returning response: {}", response);
        return response;
    }

    /**
     * Fetches usage examples for the provided word using the ChatGPT API.
     *
     * @param word the word for which to fetch usage examples.
     * @return a string containing usage examples of the word.
     */
    public String fetchUsageExamples (String word){
        log.debug("Entering fetchUsageExamples (String word)");

        String promt = constructPrompt(word, ChatGptPromptsEnum.USAGEEXAMPLES);
        return chat(promt);
    }

    /**
     * Fetches the context for the provided English and Russian words using the ChatGPT API.
     *
     * @param englishWord the English word for which to fetch the context.
     * @param russianWord the Russian word for which to fetch the context.
     * @return a string containing the context of the words.
     */
    public String fetchWordContext (String englishWord, String russianWord) {
        log.debug("Entering fetchWordContext (String englishWord, String russianWord)");

        String word = englishWord + " - " + russianWord;
        String promt = constructPrompt(word, ChatGptPromptsEnum.CONTEXT);
        return chat(promt);
    }

    /**
     * Constructs a prompt for the ChatGPT API based on the provided word and prompt type.
     *
     * @param word the word to include in the prompt.
     * @param promptType the type of the prompt.
     * @return a string containing the constructed prompt.
     */
    private String constructPrompt(String word, ChatGptPromptsEnum promptType) {
        log.debug("Entering constructPrompt(String word, ChatGptPromptsEnum promptType)");

        return promptType
                .getPrompt()
                .formatted(word);
    }

    /**
     * Checks if the provided response matches the predefined pattern.
     *
     * @param response the response to check.
     * @return a boolean value indicating whether the response matches the pattern.
     */
    private boolean doesMatchPattern(String response) {
        log.debug("Entering doesMatchPattern(String response)");

        Matcher matcher = pattern.matcher(response);
        return matcher.matches();
    }

    /**
     * Splits the provided response into parts based on the delimiter.
     *
     * @param response the response to split.
     * @return an array of strings containing the split parts of the response.
     */
    private String[] splitResponse(String response) {
        log.debug("Entering splitResponse(String response)");

        return response.split("\\. ");
    }

    /**
     * Extracts a Word object from the provided string.
     *
     * @param str the string from which to extract the Word object.
     * @return a Word object containing the extracted words.
     */
    private Word extractWordFromResponse(String str) {
        log.debug("Entering extractWordFromResponse(String str)");

        String[] splitStr = str.replaceAll("\\.", "").split(", ");

        String russianWord = splitStr[0].substring(4); // 4 - длина строки "ru: "
        String englishWord = splitStr[1].substring(4); // 4 - длина строки "en: "

        Word word = Word.builder()
                .russianWord(russianWord)
                .englishWord(englishWord)
                .build();
        log.debug("Returning word: {}", word);
        return word;
    }
}
