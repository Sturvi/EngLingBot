package com.example.englingbot.mapper;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserWordList;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.WordListTypeEnum;

public class UserWordListMapper {

    public static UserWordList mapNewWordInUserWordList(Word word, AppUser appUser) {
        return UserWordList
                .builder()
                .listType(WordListTypeEnum.LEARNING)
                .timerValue(0)
                .user(appUser)
                .word(word)
                .build();
    }
}
