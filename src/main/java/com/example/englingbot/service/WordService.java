package com.example.englingbot.service;

import com.example.englingbot.model.Word;
import com.example.englingbot.repository.WordRepository;
import com.example.englingbot.service.externalapi.chatgpt.ChatGptWordUtils;
import com.example.englingbot.service.externalapi.googleapi.GoogleTranslator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        log.debug("Starting translation for incoming word: {}", incomingWord);
        List<Word> newWordsList = new ArrayList<>();

        Word googleResponse = translateGoogle(incomingWord);
        if (googleResponse != null){
            newWordsList.add(googleResponse);
        }

        newWordsList.addAll(translateChatGpt(incomingWord));

        saveNewWords(newWordsList);


        log.debug("Translation completed for incoming word: {}. New words list size: {}", incomingWord, newWordsList.size());
        return newWordsList;
    }

    public List<Word> fetchWordList (String word){
        return wordRepository.findByRussianWordOrEnglishWord(word);
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
        List<Word> newWordsList = new ArrayList<>();
        List<Word> chatGptResponse = chatGptWordUtils.getTranslations(incomingWord);

        for (Word word : chatGptResponse) {
            Optional<Word> wordFromDBOpt = wordRepository.findByRussianWordAndEnglishWord(word.getRussianWord(), word.getEnglishWord());
            if (wordFromDBOpt.isEmpty()) {
                log.info("New word from ChatGpt: {}", word);
                if (word.getRussianWord().equals(incomingWord) || word.getEnglishWord().equals(incomingWord)){
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
                .russianWord(wordMap.get("ru").toLowerCase())
                .englishWord(wordMap.get("en").toLowerCase())
                .build();
    }

    private void addTranscription (Word word) {
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

    public String getWordBetweenSpaces(String input) {
        int startIndex = input.indexOf('\'') + 1;
        int endIndex = input.lastIndexOf('\'');

        if (startIndex >= endIndex) {
            return "";
        }

        return input.substring(startIndex, endIndex);
    }
}
