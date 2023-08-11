package com.example.englingbot.service.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TextMessageComposer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleStudyWordsListCommand implements SomeMessageHandler {
    private final UserVocabularyService userVocabularyService;
    private final TextMessageComposer textMessageComposer;
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Entering handle method");
        appUser.setUserState(UserStateEnum.LIST_LEARNING_WORDS);

        var wordList = userVocabularyService.getUserWordListByType(appUser, UserWordState.LEARNING);
        log.debug("Fetched user learning word list: {}", wordList);

        var messageText = textMessageComposer.LearningWordsMessageText(wordList);
        log.debug("Composed user learning word list message: {}", messageText);

        messageService.sendMessage(botEvent.getId(), messageText);
        log.trace("Exiting handle method");
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.LIST_STUDY_WORDS;
    }
}
