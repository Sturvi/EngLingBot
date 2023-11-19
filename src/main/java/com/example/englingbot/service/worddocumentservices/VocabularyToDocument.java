package com.example.englingbot.service.worddocumentservices;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class VocabularyToDocument extends TempWordDocument {

    public VocabularyToDocument() {
        super();
    }

    public Optional<File> getDocumentWithVocabulary(AppUser appUser, List<UserVocabulary> userVocabularyList) {
        setDocumentName(appUser.getFirstName() + "'s Vocabulary");
        return createVocabularyDocument(userVocabularyList);
    }

    private Optional<File> createVocabularyDocument(List<UserVocabulary> wordList) {
        if (wordList.isEmpty()) {
            return Optional.empty();
        }

        Map<Boolean, List<UserVocabulary>> partitionedWords = wordList.stream()
                .collect(Collectors.partitioningBy(uv -> uv.getListType() == UserWordState.LEARNED));

        List<UserVocabulary> leanedWordsList = partitionedWords.get(true);
        List<UserVocabulary> learningWordsList = partitionedWords.get(false);

        if (learningWordsList.isEmpty() && leanedWordsList.isEmpty()) {
            return Optional.empty();
        }

        Map<Integer, List<UserVocabulary>> groupedWords = learningWordsList.stream()
                .collect(Collectors.groupingBy(UserVocabulary::getTimerValue, TreeMap::new, Collectors.toList()));

        groupedWords.forEach(this::addToDocumentRepetitionWords);

        addToDocumentLearnedWord(leanedWordsList);

        return Optional.of(getFile());
    }

    private void addToDocumentLearnedWord(List<UserVocabulary> userVocabularyList) {
        addSectionTitle("Learned Words");
        addWordsToDocument(userVocabularyList);
    }

    private void addToDocumentRepetitionWords(Integer wordLevel, List<UserVocabulary> userVocabularyList) {
        addSectionTitle("Words Level " + wordLevel);
        addWordsToDocument(userVocabularyList);
    }

    private void addWordsToDocument(List<UserVocabulary> userVocabularyList) {
        userVocabularyList.forEach(e -> addWordToDocument(e.getWord()));
        addNewLine();
    }

    private void addWordToDocument(Word word) {
        String transcription = word.getTranscription() == null ? "" : word.getTranscription() + " ";
        String wordString = word.getEnglishWord() + " " + transcription + " - " + word.getRussianWord();

        addText(wordString);
    }
}
