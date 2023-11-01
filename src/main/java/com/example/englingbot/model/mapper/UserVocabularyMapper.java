package com.example.englingbot.model.mapper;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper class responsible for mapping user's word list.
 */
@Slf4j
@Component
public class UserVocabularyMapper {

    /**
     * Maps a new word into the user's vocabulary list.
     *
     * @param word The word to be mapped.
     * @param appUser The user to whom the word will be mapped.
     * @return The newly mapped user vocabulary.
     */
    public UserVocabulary mapNewWordInUserWordList(Word word, AppUser appUser) {
        log.debug("Mapping new word in user word list.");
        return UserVocabulary
                .builder()
                .listType(UserWordState.LEARNING)
                .timerValue(0)
                .user(appUser)
                .word(word)
                .lastRetry(LocalDateTime.now())
                .failedAttempts(0)
                .build();
    }
}
