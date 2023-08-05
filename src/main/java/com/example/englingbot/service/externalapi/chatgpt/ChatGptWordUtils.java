package com.example.englingbot.service.externalapi.chatgpt;

import com.example.englingbot.model.Word;
import com.example.englingbot.service.WordService;
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
public class ChatGptWordUtils extends ChatGpt {
    private static final Pattern pattern = Pattern.compile("(ru:\\s?[а-яёА-ЯЁ]+,\\s?en:\\s?[a-zA-Z]+\\.\\s?)+");

    /**
     * Constructor for the ChatGptWordUtils class.
     *
     * @param webClient WebClient instance for making HTTP requests.
     * @param gson      Gson instance for parsing JSON.
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
    public List<Word> getTranslations(String incomingWord) {
        log.debug("Entering getTranslations (String incomingWord)");

        String prompt = constructPrompt(incomingWord, ChatGptPromptsEnum.NEWWORD);
        Request request = new Request(prompt, RequestPriorityEnum.TRANSLATION.getPriority());
        addRequest(request);

        var apiResponse = waitingResponse(request);
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
     * Fetches the transcription for the provided word.
     *
     * @param word the word to transcribe.
     * @return the transcription of the word.
     */
    public String fetchTranscription(String word) {
        return fetchTranscription(word, RequestPriorityEnum.TRANSCRIPTION.getPriority());
    }

    /**
     * Fetches the transcription for the provided word with priority.
     *
     * @param word the word to transcribe.
     * @return the transcription of the word.
     */
    public String fetchTranscriptionWithPriority(String word) {
        return fetchTranscription(word, RequestPriorityEnum.PRIORITYTRANSCRIPTION.getPriority());
    }

    /**
     * Fetches the transcription for the provided word with specified priority.
     *
     * @param word     the word to transcribe.
     * @param priority the priority level.
     * @return the transcription of the word.
     */
    private String fetchTranscription(String word, int priority) {
        log.debug("Entering fetchTranscription(String word)");

        log.info("Getting transcription for: {}", word);

        String prompt = constructPrompt(word, ChatGptPromptsEnum.TRANSCRIPTIONS);
        log.debug("Created prompt: {}", prompt);

        Request request = new Request(prompt, priority);
        addRequest(request);
        String response = waitingResponse(request);

        log.debug("Received response: {}", response);

        if (!response.matches("^\\[.*\\]$")) {
            log.debug("Response matches regular expression, returning null.");
            return null;
        }

        response.replaceAll("\\[+", "[").replaceAll("\\]+", "]");

        log.info("Returning transcription: {}", response);
        log.debug("Returning response: {}", response);
        return response;
    }

    /**
     * Fetches the usage examples for the provided word.
     *
     * @param word the word to fetch usage examples for.
     * @return the usage examples of the word.
     */
    public String fetchUsageExamples(String word) {
        return fetchUsageExamples(word, RequestPriorityEnum.USAGEEXAMPLES.getPriority());
    }

    /**
     * Fetches the usage examples for the provided word with priority.
     *
     * @param word the word to fetch usage examples for.
     * @return the usage examples of the word.
     */
    public String fetchUsageExamplesWithPriority(String word) {
        return fetchUsageExamples(word, RequestPriorityEnum.PRIORITYUSAGEEXAMPLES.getPriority());
    }

    /**
     * Fetches the usage examples for the provided word with specified priority.
     *
     * @param word     the word to fetch usage examples for.
     * @param priority the priority level.
     * @return the usage examples of the word.
     */
    private String fetchUsageExamples(String word, int priority) {
        log.debug("Entering fetchUsageExamples(String word)");

        String promt = constructPrompt(word, ChatGptPromptsEnum.USAGEEXAMPLES);

        Request request = new Request(promt, priority);
        addRequest(request);

        return waitingResponse(request);
    }

    /**
     * Fetches the word context for the provided English and Russian words.
     *
     * @param englishWord the English word.
     * @param russianWord the Russian word.
     * @return the context of the words.
     */
    public String fetchWordContext(String englishWord, String russianWord) {
        return fetchWordContext(englishWord, russianWord, RequestPriorityEnum.CONTEXT.getPriority());
    }

    /**
     * Fetches the word context for the provided English and Russian words with priority.
     *
     * @param englishWord the English word.
     * @param russianWord the Russian word.
     * @return the context of the words.
     */
    public String fetchWordContextWithPriority(String englishWord, String russianWord) {
        return fetchWordContext(englishWord, russianWord, RequestPriorityEnum.PRIORITYCONTEXT.getPriority());
    }

    /**
     * Fetches the word context for the provided English and Russian words with specified priority.
     *
     * @param englishWord the English word.
     * @param russianWord the Russian word.
     * @param priority    the priority level.
     * @return the context of the words.
     */
    private String fetchWordContext(String englishWord, String russianWord, int priority) {
        log.debug("Entering fetchWordContext(String englishWord, String russianWord)");

        String word = englishWord + " - " + russianWord;
        String promt = constructPrompt(word, ChatGptPromptsEnum.CONTEXT);

        Request request = new Request(promt, priority);
        addRequest(request);

        return waitingResponse(request);
    }

    /**
     * Constructs a prompt for the ChatGPT API based on the provided word and prompt type.
     *
     * @param word       the word to include in the prompt.
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

        String russianWord = WordService.capitalizeFirstLetter(splitStr[0].substring(4)); // 4 - длина строки "ru: "
        String englishWord = WordService.capitalizeFirstLetter(splitStr[1].substring(4)); // 4 - длина строки "en: "

        Word word = Word.builder()
                .russianWord(russianWord)
                .englishWord(englishWord)
                .build();
        log.debug("Returning word: {}", word);
        return word;
    }

    /**
     * Waits for a response from the API and returns it.
     *
     * @param request the request to wait for.
     * @return the response from the API.
     */
    private String waitingResponse(Request request) {
        log.debug("Entering waitingResponse(Request request)");

        while (request.getResponse() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return request.getResponse();
    }
}
