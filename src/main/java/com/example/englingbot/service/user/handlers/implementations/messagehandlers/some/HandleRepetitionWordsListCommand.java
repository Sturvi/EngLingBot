package com.example.englingbot.service.user.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.TelegramMessageService;
import com.example.englingbot.service.message.TextMessageComposer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleRepetitionWordsListCommand implements SomeMessageHandler {
    private final UserVocabularyService userVocabularyService;
    private final TextMessageComposer textMessageComposer;
    private final TelegramMessageService telegramMessageService;
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var wordList = userVocabularyService.getUserVocabularies(appUser, UserWordState.REPETITION);

        var messageTextList = textMessageComposer.RepetitionWordsMessageText(wordList);

        messageTextList.forEach(m -> telegramMessageService.sendMessageToUser(botEvent.getId(), m));
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.LIST_REPETITION_WORDS;
    }
}
