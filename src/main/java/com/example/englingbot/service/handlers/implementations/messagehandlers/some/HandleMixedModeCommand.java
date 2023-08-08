package com.example.englingbot.service.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleMixedModeCommand implements SomeMessageHandler {
    private final UserVocabularyService userVocabularyService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        appUser.setUserState(UserStateEnum.MIXED);

        userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING, UserWordState.REPETITION);
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.MIXED_MODE;
    }
}
