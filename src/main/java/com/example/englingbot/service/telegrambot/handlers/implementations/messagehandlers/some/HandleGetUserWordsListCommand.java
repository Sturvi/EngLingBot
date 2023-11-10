package com.example.englingbot.service.telegrambot.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.telegrambot.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import com.example.englingbot.service.worddocumentservices.VocabularyToDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleGetUserWordsListCommand implements SomeMessageHandler {
    private final UserVocabularyService userVocabularyService;
    private final TelegramMessageService telegramMessageService;
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var wordList = userVocabularyService.getUserVocabularies(appUser, UserWordState.LEARNING, UserWordState.LEARNED);

        var vocabularyFile = new VocabularyToDocument().getDocumentWithVocabulary(appUser, wordList);

        vocabularyFile.ifPresent(file ->
                telegramMessageService.sendDocumentToUser(appUser.getTelegramChatId(), "User Vocabulary", file));
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.LIST_REPETITION_WORDS;
    }
}
