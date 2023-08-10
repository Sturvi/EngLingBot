package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleNextCommand implements SomeCallbackQueryHandler {
    private final UserVocabularyService userVocabularyService;
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Next' command for bot event: {}", botEvent);
        var userState = appUser.getUserState();

        switch (userState) {
            case LEARNING -> userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING);
            case REPETITION ->
                    userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.REPETITION);
            case MIXED ->
                    userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING, UserWordState.REPETITION);
            default ->
                    messageService.sendMessage(botEvent.getId(), "Для изучения слов выберите пожалуйста соответствующее меню");
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NEXT;
    }
}
