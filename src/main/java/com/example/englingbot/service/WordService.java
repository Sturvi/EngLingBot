package com.example.englingbot.service;

import com.example.englingbot.model.Word;
import com.example.englingbot.repository.WordRepository;
import com.example.englingbot.service.externalapi.chatgpt.ChatGptWordUtils;
import com.example.englingbot.service.externalapi.googleapi.GoogleTranslator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class WordService {

    private final GoogleTranslator googleTranslator;
    private final ChatGptWordUtils chatGptWordUtils;
    private final ExecutorService chatGPTExecutorService;
    private final WordRepository wordRepository;


    public WordService(GoogleTranslator googleTranslator, ChatGptWordUtils chatGptWordUtils, ExecutorService chatGPTExecutorService, WordRepository wordRepository) {
        this.googleTranslator = googleTranslator;
        this.chatGptWordUtils = chatGptWordUtils;
        this.chatGPTExecutorService = chatGPTExecutorService;
        this.wordRepository = wordRepository;
    }

    public List<Word> addNewWordFromExternalApi(String incomingWord) {
        incomingWord = capitalizeFirstLetter(incomingWord);
        log.debug("Starting translation for incoming word: {}", incomingWord);
        Set<Word> newWordsSet = new HashSet<>();
        List<Word> availableWords = wordRepository.findByRussianWordOrEnglishWord(incomingWord);

        Word googleResponse = translateGoogle(incomingWord);
        if (googleResponse != null && !availableWords.contains(googleResponse)) {
            newWordsSet.add(googleResponse);
        }

        var wordsFromChatGpt = translateChatGpt(incomingWord);

        for (Word word :
                wordsFromChatGpt) {
            if (!availableWords.contains(word)){
                newWordsSet.add(word);
            }
        }

        var newWordsList = newWordsSet.stream().toList();

        saveNewWords(newWordsList);

        log.debug("Translation completed for incoming word: {}. New words list size: {}", incomingWord, newWordsSet.size());
        return newWordsList;
    }

    public List<Word> fetchWordList(String word) {
        word = capitalizeFirstLetter(word);
        return wordRepository.findByRussianWordOrEnglishWord(word);
    }

    public Word getWordByTextMessage (String textMessage){
        String[] splitedMessage = textMessage.split("  -  ");
        var word = wordRepository.findByRussianWordAndEnglishWord(splitedMessage[1], splitedMessage[0]);
        return word.get();
    }

    private void saveNewWords(List<Word> newWordsList) {
        for (Word word : newWordsList) {
            try {
                var wordFromDB = wordRepository.findByRussianWordAndEnglishWord(word.getRussianWord(), word.getEnglishWord());
                if (wordFromDB.isPresent()) continue;

                wordRepository.save(word);
                addTranscription(word);

            } catch (DataIntegrityViolationException e) {
                log.error("Failed to save the word due to integrity violation: {}", word, e);
            }
        }
    }

    private Word translateGoogle(String incomingWord) {
        incomingWord = capitalizeFirstLetter(incomingWord);
        Map<String, String> translation = googleTranslator.translate(incomingWord);
        Word word = getWordFromMap(translation);

        Optional<Word> wordFromDBOpt = wordRepository.findByRussianWordAndEnglishWord(word.getRussianWord(), word.getEnglishWord());
        if (wordFromDBOpt.isEmpty()) {
            log.info("New word from Google translate: {}", word);
            return word;
        } else {
            log.warn("Word from Google translate already exists in database: {}", word);
            return null;
        }
    }

    private List<Word> translateChatGpt(String incomingWord) {
        incomingWord = capitalizeFirstLetter(incomingWord);
        List<Word> newWordsList = new ArrayList<>();
        List<Word> chatGptResponse = chatGptWordUtils.getTranslations(incomingWord);

        for (Word word : chatGptResponse) {
            Optional<Word> wordFromDBOpt = wordRepository.findByRussianWordAndEnglishWord(word.getRussianWord(), word.getEnglishWord());
            if (wordFromDBOpt.isEmpty()) {
                log.info("New word from ChatGpt: {}", word);
                if (word.getRussianWord().equals(incomingWord) || word.getEnglishWord().equals(incomingWord)) {
                    newWordsList.add(word);
                }
            } else {
                log.warn("Word from ChatGpt already exists in database: {}", word);
            }
        }

        if (newWordsList.isEmpty()) {
            log.error("No new words from ChatGpt for incoming word: {}", incomingWord);
        }

        return newWordsList;
    }

    private Word getWordFromMap(Map<String, String> wordMap) {
        return Word.builder()
                .russianWord(wordMap.get("ru"))
                .englishWord(wordMap.get("en"))
                .build();
    }

    private void addTranscription(Word word) {
        if (word.getTranscription() == null) {
            Runnable runnable = () -> {
                String transcription = chatGptWordUtils.fetchTranscription(word.getEnglishWord());
                if (transcription != null) {
                    word.setTranscription(transcription);
                    wordRepository.save(word);
                }
            };

            chatGPTExecutorService.submit(runnable);
        }
    }

    public String getStringBetweenSpaces(String input) {
        int startIndex = input.indexOf('\'') + 1;
        int endIndex = input.lastIndexOf('\'');

        if (startIndex >= endIndex) {
            return "";
        }

        return input.substring(startIndex, endIndex);
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
