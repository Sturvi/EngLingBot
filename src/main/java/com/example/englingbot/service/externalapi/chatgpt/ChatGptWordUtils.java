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


@Component
@Slf4j
public class ChatGptWordUtils extends ChatGpt {

    private static final Pattern pattern = Pattern.compile("(ru:\\s?[а-яёА-ЯЁ]+,\\s?en:\\s?[a-zA-Z]+\\.\\s?)+");


    public ChatGptWordUtils(WebClient webClient, Gson gson) {
        super(webClient, gson);
    }


    public List<Word> getTranslations(String incomingWord) {
        log.debug("Entering getTranslations (String incomingWord)");

        String prompt = constructPrompt(incomingWord, ChatGptPromptsEnum.NEW_WORD);

        var apiResponse = chat(prompt);
        log.debug("Получен ответ из Chat Gpt: {}", apiResponse);

        List<Word> words = new ArrayList<>();

        if (doesMatchPattern(apiResponse)) {
            log.info("The string matches the pattern");
            String[] splitResponse = splitResponse(apiResponse);

            for (String str : splitResponse) {
                Word word = extractWordFromResponse(str);

                if (wordMatchesIncomingWord(word, incomingWord)) {
                    words.add(word);
                    log.debug("Russian word: {}, English word: {}", word.getRussianWord(), word.getEnglishWord());
                }
            }
        } else {
            log.warn("The string does not match the pattern");
        }

        log.debug("Returning words: {}", words);
        return words;
    }


    public String fetchTranscription(String word) {
        log.trace("Entering fetchTranscription(String word)");

        log.info("Getting transcription for: {}", word);

        String prompt = constructPrompt(word, ChatGptPromptsEnum.TRANSCRIPTIONS);
        log.debug("Created prompt: {}", prompt);

        String response = chat(prompt);

        log.debug("Received response: {}", response);

        if (!response.matches("^\\[.*\\]$")) {
            log.debug("Response matches regular expression, returning null.");
            return null;
        }

        response = response.replaceAll("\\[+", "[").replaceAll("\\]+", "]");

        log.info("Returning transcription: {}", response);
        return response;
    }


    public String fetchUsageExamples(String word) {
        log.debug("Entering fetchUsageExamples(String word)");

        String promt = constructPrompt(word, ChatGptPromptsEnum.USAGE_EXAMPLES);

        return chat(promt);
    }

    public String fetchWordContext(String englishWord, String russianWord) {
        log.debug("Entering fetchWordContext(String englishWord, String russianWord)");

        String word = englishWord + " - " + russianWord;
        String promt = constructPrompt(word, ChatGptPromptsEnum.CONTEXT);

        return chat(promt);
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

        return promptType.getPrompt().formatted(word);
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

        Word word = Word.builder().russianWord(russianWord).englishWord(englishWord).build();
        log.debug("Returning word: {}", word);
        return word;
    }


    private boolean wordMatchesIncomingWord(Word word, String incomingWord) {
        return word.getRussianWord().equalsIgnoreCase(incomingWord) ||
                word.getEnglishWord().equalsIgnoreCase(incomingWord);
    }
}
