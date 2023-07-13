package com.example.englingbot.service;

import com.example.englingbot.model.UserEntity;
import com.example.englingbot.model.UserWordList;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.WordListTypeEnum;
import com.example.englingbot.repository.UserWordListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserWordListService {

    private final UserWordListRepository userWordListRepository;

    public List<UserWordList> getUserWordLists(UserEntity user, WordListTypeEnum... types) {
        return userWordListRepository.findByUserAndListTypeIn(user, Arrays.asList(types));
    }

    public UserWordList getRandomUserWordList(UserEntity user, WordListTypeEnum... types) {
        List<UserWordList> userWordLists = getUserWordLists(user, types);
        if (userWordLists.isEmpty()) {
            return null;  // или бросьте исключение, в зависимости от вашей бизнес-логики
        }
        Random rand = new Random();
        return userWordLists.get(rand.nextInt(userWordLists.size()));
    }

    public String getUserWordListString(UserWordList userWordList) {
        StringBuilder sb = new StringBuilder();
        sb.append("Слово из словаря \"");

        switch (userWordList.getListType()) {
            case LEARNING -> sb.append("Изучаемые слова");
            case REPETITION -> sb.append("Слова на повторении ").append(userWordList.getTimerValue()).append(" уровня");
            case LEARNED -> sb.append("Изученное слово");
        }

        sb.append("\"\n\n");

        Word word = userWordList.getWord();

        // Генерируем случайным образом формат слова
        Random rand = new Random();
        if (rand.nextBoolean()) {
            sb.append(word.getEnglishWord())
                    .append(" [")
                    .append(word.getTranscription())
                    .append("]   -   <span class='tg-spoiler'>")
                    .append(word.getRussianWord())
                    .append("</span>");
        } else {
            sb.append(word.getRussianWord())
                    .append("   -   <span class='tg-spoiler'>")
                    .append(word.getEnglishWord())
                    .append(" [")
                    .append(word.getTranscription())
                    .append("]</span>");
        }

        return sb.toString();
    }
}
