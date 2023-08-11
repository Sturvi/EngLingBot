package com.example.englingbot.service.message;

import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class TextMessageComposer {

    public String LearningWordsMessageText(List<Word> wordList) {
        if (wordList.isEmpty()) {
            return "Вы не изучаете сейчас новые слова";
        }

        StringBuilder messageText = new StringBuilder();
        messageText.append("Список слов на изучении:\n\n");

        wordList.forEach(word -> addWordToStringBuilder(messageText, word));

        return messageText.toString();
    }

    public List<String> RepetitionWordsMessageText(List<UserVocabulary> wordList) {
        Map<Integer, List<Word>> groupedWords = new TreeMap<>();

        for (UserVocabulary userVocabulary : wordList) {
            int timerValue = userVocabulary.getTimerValue();
            groupedWords
                    .computeIfAbsent(timerValue, k -> new ArrayList<>())
                    .add(userVocabulary.getWord());
        }

        if (groupedWords.isEmpty()) {
            return List.of("В настоящий момент у вас нет слов на повторении");
        }

        List<String> messageTextList = new ArrayList<>();
        for (Map.Entry<Integer, List<Word>> entry : groupedWords.entrySet()) {
            StringBuilder messageText = new StringBuilder();
            messageText.append("Слова на повторении ").append(entry.getKey()).append(" уровня:\n\n");
            for (Word word : entry.getValue()) {
                addWordToStringBuilder(messageText, word);
            }
            messageTextList.add(messageText.toString());
        }

        return messageTextList;
    }



    private void addWordToStringBuilder (StringBuilder stringBuilder, Word word){
        String transcription = word.getTranscription() == null ? "" : word.getTranscription() + " ";

        stringBuilder
                .append(word.getEnglishWord()).append(" ")
                .append(transcription).append("- ")
                .append(word.getRussianWord())
                .append("\n");
    }
}
