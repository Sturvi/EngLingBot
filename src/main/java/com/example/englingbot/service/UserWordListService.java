package com.example.englingbot.service;

import com.example.englingbot.model.UserEntity;
import com.example.englingbot.model.UserWordList;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.WordListTypeEnum;
import com.example.englingbot.repository.UserWordListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;
import java.util.Random;

/**
 * Service for handling user word lists
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserWordListService {

    private final UserWordListRepository userWordListRepository;

    /**
     * Retrieves a list of UserWordList objects filtered by user and list type.
     *
     * @param user the UserEntity object
     * @param types the WordListTypeEnum array
     * @return a list of UserWordList objects
     */
    public List<UserWordList> getUserWordLists(UserEntity user, WordListTypeEnum... types) {
        log.info("Getting word lists for user id: {} with list types: {}", user.getId(), Arrays.toString(types));
        return userWordListRepository.findByUserAndListTypeIn(user, Arrays.asList(types));
    }

    /**
     * Retrieves a random UserWordList object filtered by user and list type.
     *
     * @param user the UserEntity object
     * @param types the WordListTypeEnum array
     * @return a UserWordList object or null if the list is empty
     */
    public UserWordList getRandomUserWordList(UserEntity user, WordListTypeEnum... types) {
        log.info("Getting random word list for user id: {} with list types: {}", user.getId(), Arrays.toString(types));
        List<UserWordList> userWordLists = getUserWordLists(user, types);
        if (userWordLists.isEmpty()) {
            log.warn("User id: {} has no word lists of the requested types", user.getId());
            return null;
        }
        Random rand = new Random();
        return userWordLists.get(rand.nextInt(userWordLists.size()));
    }

    /**
     * Generates a string representation of the user word list.
     *
     * @param userWordList the UserWordList object
     * @return a string representation of the UserWordList
     */
    public String getUserWordListString(UserWordList userWordList) {
        log.info("Generating string for word list id: {}", userWordList.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("Слово из словаря \"");

        switch (userWordList.getListType()) {
            case LEARNING -> sb.append("Изучаемые слова");
            case REPETITION -> sb.append("Слова на повторении ").append(userWordList.getTimerValue()).append(" уровня");
            case LEARNED -> sb.append("Изученное слово");
        }

        sb.append("\"\n\n");

        Word word = userWordList.getWord();

        // Randomly generate the word format
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

        log.info("Generated string for word list id: {}", userWordList.getId());
        return sb.toString();
    }
}
