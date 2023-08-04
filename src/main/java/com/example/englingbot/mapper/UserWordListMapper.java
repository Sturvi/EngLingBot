package com.example.englingbot.mapper;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;

public class UserWordListMapper {

    public static UserVocabulary mapNewWordInUserWordList(Word word, AppUser appUser) {
        return UserVocabulary
                .builder()
                .listType(UserWordState.LEARNING)
                .timerValue(0)
                .user(appUser)
                .word(word)
                .build();
    }
}
