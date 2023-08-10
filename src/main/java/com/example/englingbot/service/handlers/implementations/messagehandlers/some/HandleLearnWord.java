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
public class HandleLearnWord implements SomeMessageHandler {
    private final UserVocabularyService userVocabularyService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handle LearnWord command for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.LEARNING);

        userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING);

        log.debug("Finished handle LearnWord command for event: {}", botEvent);
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.LEARN_WORD;
    }
}
