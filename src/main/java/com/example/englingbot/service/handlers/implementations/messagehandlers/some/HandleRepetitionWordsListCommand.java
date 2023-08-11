package com.example.englingbot.service.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.MessageService;
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
    private final MessageService messageService;
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var wordList = userVocabularyService.getUserVocabularies(appUser, UserWordState.REPETITION);

        var messageTextList = textMessageComposer.RepetitionWordsMessageText(wordList);

        messageTextList.forEach(m -> messageService.sendMessage(botEvent.getId(), m));
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.LIST_REPETITION_WORDS;
    }
}
