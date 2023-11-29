package com.example.englingbot.service;

import com.example.englingbot.model.dto.WordDto;
import com.example.englingbot.model.dto.converter.WordConverter;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.dto.converter.WordReviewConverter;
import com.example.englingbot.repository.WordRepository;
import com.example.englingbot.repository.WordReviewRepository;
import com.example.englingbot.service.externalapi.openai.ChatGptWordUtils;
import com.example.englingbot.service.externalapi.googleapi.GoogleTranslator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This class provides various methods for managing words in the system.
 */
@Slf4j
@Service
public class WordService {

    private final GoogleTranslator googleTranslator;
    private final ChatGptWordUtils chatGptWordUtils;

    private final ExecutorService chatGPTExecutorService;
    private final WordRepository wordRepository;
    private final WordReviewRepository wordReviewRepository;

    public WordService(GoogleTranslator googleTranslator,
                       ChatGptWordUtils chatGptWordUtils,
                       @Qualifier("chatGPTExecutorService")
                       ExecutorService chatGPTExecutorService,
                       WordRepository wordRepository, WordReviewRepository wordReviewRepository) {
        this.googleTranslator = googleTranslator;
        this.chatGptWordUtils = chatGptWordUtils;
        this.chatGPTExecutorService = chatGPTExecutorService;
        this.wordRepository = wordRepository;
        this.wordReviewRepository = wordReviewRepository;
    }

    /**
     * Adds a new word from an external API to the repository.
     *
     * @param incomingWord The incoming word to be processed.
     * @return The list of new words added to the repository.
     */
    public List<Word> addNewWordFromExternalApi(String incomingWord) {
        log.debug("Processing incomingWord: {}", incomingWord);
        incomingWord = capitalizeFirstLetter(incomingWord);

        Set<Word> newWordsSet = new HashSet<>();

        try {
            Word googleResponse = translateGoogle(incomingWord);
            newWordsSet.add(googleResponse);
        } catch (Exception e) {
            log.warn("No response received from Google translate for word: {}. Error: {}", incomingWord, e.getMessage());
        }

        try {
            List<Word> wordsFromChatGpt = translateChatGpt(incomingWord);
            newWordsSet.addAll(wordsFromChatGpt);
        } catch (Exception e) {
            log.warn("No response received from ChatGpt for word: {}. Error: {}", incomingWord, e.getMessage());
        }

        var newWordsList = new ArrayList<>(newWordsSet);
        log.info("Saving {} new words to the repository", newWordsList.size());
        saveNewWords(newWordsList);

        return newWordsList;
    }

    /**
     * Fetches a list of words from the repository based on the provided word.
     *
     * @param word The word to search for in the repository.
     * @return The list of words found in the repository that match the provided word.
     */
    public List<Word> fetchWordList(String word) {
        word = capitalizeFirstLetter(word);
        log.trace("Fetching word list for word: {}", word);
        return wordRepository.findByRussianWordOrEnglishWord(word);
    }

    /**
     * Retrieves a word from the repository based on the provided text message.
     *
     * @param textMessage The text message containing two words separated by ' - '.
     * @return An Optional object containing the word found in the repository if it matches the provided text message,
     *         or an empty Optional if the text message format is incorrect or no matching word is found.
     */
    public Optional<Word> getWordByTextMessage(String textMessage) {
        log.trace("Getting word by text message: {}", textMessage);

        String[] splitedMessage = textMessage.split(" {2}- {2}");

        if (splitedMessage.length != 2) {
            log.debug("Text message format is incorrect. Expected 2 words separated by ' - ', but got: {}", textMessage);
            return Optional.empty();  // Return empty optional if we don't have exactly 2 words.
        }

        String firstWord = splitedMessage[0].trim();
        String secondWord = splitedMessage[1].trim();
        log.trace("Parsed words: First word - {}, Second word - {}", firstWord, secondWord);

        var wordOptional = findWord(firstWord, secondWord);

        wordOptional.ifPresent(word -> log.debug("Found word: {}", word));
        wordOptional.ifPresentOrElse(
                word -> log.trace("Adding extra information to word if necessary"),
                () -> log.warn("No word found for the provided text message")
        );

        wordOptional.ifPresent(this::addExtraInformationIfNecessary);

        return wordOptional;
    }

    public Optional<Word> getWord (Long wordId) {
        var wordOpt = wordRepository.findById(wordId);

        wordOpt.ifPresent(this::addExtraInformationIfNecessary);

        return wordOpt;
    }

    /**
     * Retrieves a word from the repository based on the provided Russian and English combination.
     *
     * @param first  The Russian word.
     * @param second The English word.
     * @return An Optional object containing the word found in the repository if it matches the provided Russian and English combination,
     *         or an empty Optional if no matching word is found.
     */
    private Optional<Word> findWord(String first, String second) {
        log.trace("Finding word by Russian and English combination: {} and {}", first, second);

        return wordRepository.findByRussianWordAndEnglishWord(second, first)
                .or(() -> {
                    log.debug("Did not find word with Russian: {} and English: {}. Trying opposite combination.", second, first);
                    return wordRepository.findByRussianWordAndEnglishWord(first, second);
                });
    }

    /**
     * Adds extra information to a word if necessary.
     *
     * @param word The word to check and add extra information to.
     */
    private void addExtraInformationIfNecessary(Word word) {
        log.trace("Checking if extra information needs to be added for word: {}", word);

        if (word.getTranscription() == null ||
                word.getContext() == null ||
                word.getUsageExamples() == null) {
            log.debug("Adding extra information to word: {}", word);
            addExtraInformation(word);
        } else {
            log.trace("Word already contains necessary information. No need to add extra.");
        }
    }

    /**
     * Saves a list of new words.
     *
     * @param newWordsList The list of new words to be saved.
     */
    private void saveNewWords(List<Word> newWordsList) {
        if (newWordsList == null || newWordsList.isEmpty()) {
            log.warn("The newWordsList provided for saving is empty or null.");
            return;
        }

        log.info("Starting to save new words.");

        for (Word word : newWordsList) {
            try {
                var wordFromDB = wordRepository.findByRussianWordAndEnglishWord(word.getRussianWord(), word.getEnglishWord());
                if (wordFromDB.isPresent()) continue;

                sendToReview(word);

                wordRepository.save(word);
                addExtraInformation(word);

            } catch (DataIntegrityViolationException e) {
                log.error("Failed to save the word due to integrity violation: {}", word, e);
            } catch (Exception e) {
                log.error("Unexpected error while saving the word: {}", word, e);
            }
        }

        log.info("Finished saving new words.");
    }

    /**
     * Sends a word for review.
     *
     * @param word The word to be sent for review.
     */
    private void sendToReview(Word word) {
        Runnable runnable = () -> {
            var wordReviewDTO = chatGptWordUtils.reviewWordWithChatGpt(word);
            var wordReview = WordReviewConverter.toEntity(wordReviewDTO);
            wordReviewRepository.save(wordReview);
        };

        chatGPTExecutorService.submit(runnable);
    }

    /**
     * Translates a word using Google Translate API.
     *
     * @param incomingWord The word to be translated.
     * @return The translated word as a Word object, or null if the word already exists in the database.
     */
    private Word translateGoogle(String incomingWord) {
        log.trace("Entering translateGoogle method");

        var wordDto = googleTranslator.translate(incomingWord);
        Word word = WordConverter.toEntity(wordDto);
        Optional<Word> wordFromDBOpt = wordRepository.findByRussianWordAndEnglishWord(wordDto.getRussianWord(), wordDto.getEnglishWord());

        if (wordFromDBOpt.isEmpty() &&
                (wordDto.getEnglishWord().equals(incomingWord) ||
                        wordDto.getRussianWord().equals(incomingWord))) {
            log.info("New word from Google translate: {}", word);

            return word;
        } else {
            log.warn("Word from Google translate already exists in database: {}", word);
            return null;
        }
    }

    /**
     * Translates a word using ChatGpt word translation service.
     *
     * @param incomingWord The word to be translated.
     * @return A list of translated words as Word objects, or an empty list if no new words are found.
     */
    private List<Word> translateChatGpt(String incomingWord) {
        log.trace("Entering translateChatGpt method");

        List<Word> newWordsList = new ArrayList<>();
        List<WordDto> chatGptResponse = chatGptWordUtils.getTranslations(incomingWord);

        for (WordDto wordDto : chatGptResponse) {
            Optional<Word> wordFromDBOpt = wordRepository.findByRussianWordAndEnglishWord(wordDto.getRussianWord(), wordDto.getEnglishWord());
            if (wordFromDBOpt.isEmpty()) {
                log.info("New word from ChatGpt: {}", wordDto);
                Word word = WordConverter.toEntity(wordDto);
                newWordsList.add(word);
            } else {
                log.warn("Word from ChatGpt already exists in database: {}", wordDto);
            }
        }

        if (newWordsList.isEmpty()) {
            log.error("No new words from ChatGpt for incoming word: {}", incomingWord);
        }

        return newWordsList;
    }

    /**
     * Adds supplementary information to a given word object including its transcription, context, and usage examples.
     *
     * <p>It has been observed that certain pieces of data might be missing, but they are not critical for the
     * immediate operations of the program. Since reaching out to the API to fetch this data can be time-consuming,
     * the method is designed to run these fetch operations in a separate thread. This ensures the main execution thread
     * remains unblocked, offering a more responsive user experience.</p>
     *
     * @param word The word object to be updated. This object may already have some fields populated.
     */
    private void addExtraInformation(Word word) {
        log.trace("Adding extra information for word: {}", word.getEnglishWord());

        Runnable runnable = () -> {
            try {
                fetchAndSetIfAbsent(word,
                        Word::getTranscription,
                        w -> fetchTranscription(w.getEnglishWord()),
                        Word::setTranscription,
                        "Transcription");

                fetchAndSetIfAbsent(word,
                        Word::getContext,
                        this::fetchContext,
                        Word::setContext,
                        "Context");

                fetchAndSetIfAbsent(word,
                        Word::getUsageExamples,
                        this::fetchUsageExamples,
                        Word::setUsageExamples,
                        "Usage examples");

                log.trace("Saving word information to repository for word: {}", word.getEnglishWord());
                wordRepository.save(word);
            } catch (Exception e) {
                log.error("Error while adding extra information for word: {}", word.getEnglishWord(), e);
            }
        };

        log.trace("Submitting task to executor service for word: {}", word.getEnglishWord());
        chatGPTExecutorService.submit(runnable);
    }

    /**
     * Fetches data if it is absent in the word object and sets the fetched data.
     *
     * @param <T>     The type of data to be fetched and set.
     * @param word    The word object to be updated.
     * @param getter  A function to get the current value from the word object.
     * @param fetcher A function to fetch the data.
     * @param setter  A function to set the fetched data to the word object.
     * @param type    Type of the data for logging purposes.
     */
    private <T> void fetchAndSetIfAbsent(Word word,
                                         Function<Word, T> getter,
                                         Function<Word, T> fetcher,
                                         BiConsumer<Word, T> setter,
                                         String type) {
        if (getter.apply(word) == null) {
            log.trace("Fetching {} for word: {}", type, word.getEnglishWord());
            T data = fetcher.apply(word);
            if (data != null) {
                setter.accept(word, data);
            }
            log.debug("{} for word {} is: {}", type, word.getEnglishWord(), data);
        }
    }

    /**
     * Fetches the transcription of the given English word.
     *
     * @param englishWord English word for which transcription needs to be fetched.
     * @return Transcription of the given English word.
     */
    private String fetchTranscription(String englishWord) {
        return chatGptWordUtils.fetchTranscription(englishWord);
    }

    private String fetchContext(Word word) {
        return chatGptWordUtils.fetchWordContext(word.toString());
    }

    private String fetchUsageExamples(Word word) {
        return chatGptWordUtils.fetchUsageExamples(word.toString());
    }

    public void addUsageExamples(Word word) {
        if (word.getUsageExamples() == null) {
            log.trace("Checking if usage examples are available for word: {}", word.getEnglishWord());
            String usageExamples = chatGptWordUtils.fetchUsageExamples(word.getEnglishWord());
            if (usageExamples != null) {
                log.debug("Usage examples found for word: {}", word.getEnglishWord());
                word.setUsageExamples(usageExamples);
                wordRepository.save(word);
                log.info("Saved updated word with usage examples: {}", word.getEnglishWord());
            } else {
                log.debug("No usage examples found for word: {}", word.getEnglishWord());
            }
        }
    }

    /**
     * Adds the context information to the specified Word object.
     *
     * @param word The Word object to which the context information will be added.
     */
    public void addWordContext(Word word) {
        log.trace("Entering addWordContext method");
        if (word.getContext() == null) {
            log.debug("Word context is null. Fetching word context from chatGptWordUtils.");
            String wordContext = chatGptWordUtils.fetchWordContext(word.toString());
            if (wordContext != null) {
                log.debug("Word context fetched successfully. Setting word context and saving word to repository.");
                word.setContext(wordContext);
                wordRepository.save(word);
            } else {
                log.debug("Failed to fetch word context. Word context remains null.");
            }
        }
        log.trace("Exiting addWordContext method");
    }

    /**
     * Capitalizes the first letter of a given string.
     *
     * @param str the string to be capitalized
     * @return the capitalized string if the input string is not null or empty; otherwise, returns the input string
     */
    public static String capitalizeFirstLetter(String str) {
        log.trace("Entering capitalizeFirstLetter method");
        if (str == null || str.isEmpty()) {
            log.debug("Input string is null or empty");
            return str;
        }

        String capitalizedStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        log.debug("Capitalized string: " + capitalizedStr);

        log.trace("Exiting capitalizeFirstLetter method");
        return capitalizedStr;
    }

    /**
     * Retrieves ten random words that are not present in the user's vocabulary.
     *
     * @param appUser the user for which to retrieve the words
     * @return a list of ten Word objects that are not present in the user's vocabulary
     */
    public List<Word> getTenRandomNewWord(AppUser appUser) {
        return wordRepository.findTenRandomWordsNotInUserVocabulary(appUser.getId());
    }

    /**
     * Deletes a given word from the repository.
     *
     * @param word the word to be deleted
     */
    public void deleteWord (Word word) {
        wordRepository.delete(word);
    }
}
