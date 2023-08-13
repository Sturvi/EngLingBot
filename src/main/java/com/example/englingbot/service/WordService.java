package com.example.englingbot.service;

import com.example.englingbot.dto.WordDto;
import com.example.englingbot.dto.converter.WordConverter;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.repository.WordRepository;
import com.example.englingbot.repository.WordReviewRepository;
import com.example.englingbot.service.externalapi.chatgpt.ChatGptWordUtils;
import com.example.englingbot.service.externalapi.googleapi.GoogleTranslator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

    public List<Word> addNewWordFromExternalApi(String incomingWord) {
        log.debug("Processing incomingWord: {}", incomingWord);
        incomingWord = capitalizeFirstLetter(incomingWord);

        Set<Word> newWordsSet = new HashSet<>();

        Word googleResponse = translateGoogle(incomingWord);
        if (googleResponse == null) {
            log.warn("No response received from Google translate for word: {}", incomingWord);
        } else {
            newWordsSet.add(googleResponse);
        }

        List<Word> wordsFromChatGpt = translateChatGpt(incomingWord);

        newWordsSet.addAll(wordsFromChatGpt);

        var newWordsList = new ArrayList<>(newWordsSet);
        log.info("Saving {} new words to the repository", newWordsList.size());
        saveNewWords(newWordsList);

        return newWordsList;
    }

    public List<Word> fetchWordList(String word) {
        word = capitalizeFirstLetter(word);
        log.trace("Fetching word list for word: {}", word);
        return wordRepository.findByRussianWordOrEnglishWord(word);
    }

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

    private Optional<Word> findWord(String first, String second) {
        log.trace("Finding word by Russian and English combination: {} and {}", first, second);

        return wordRepository.findByRussianWordAndEnglishWord(second, first)
                .or(() -> {
                    log.debug("Did not find word with Russian: {} and English: {}. Trying opposite combination.", second, first);
                    return wordRepository.findByRussianWordAndEnglishWord(first, second);
                });
    }

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

    private void sendToReview(Word word) {
        Runnable runnable = () -> {
            var wordReview = chatGptWordUtils.reviewWordWithChatGpt(word);
            wordReviewRepository.save(wordReview);
        };

        chatGPTExecutorService.submit(runnable);
    }

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

    public List<Word> getTenRandomNewWord(AppUser appUser) {
        return wordRepository.findTenRandomWordsNotInUserVocabulary(appUser.getId());
    }
}
