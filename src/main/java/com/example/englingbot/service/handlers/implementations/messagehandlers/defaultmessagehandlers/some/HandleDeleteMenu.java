package com.example.englingbot.service.handlers.implementations.messagehandlers.defaultmessagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeDefaultMessageHandler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleDeleteMenu implements SomeDefaultMessageHandler {
    private final UserVocabularyService userVocabularyService;
    private final WordService wordService;
    private final MessageService messageService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Handling bot event: {}", botEvent);
        String incomingWord = botEvent.getText();
        log.debug("Incoming word: {}", incomingWord);

        var wordList = wordService.fetchWordList(incomingWord);
        log.debug("Fetched word list: {}", wordList);

        var userVocabularyList = userVocabularyService.getUserVocabularies(appUser, wordList);
        log.debug("Fetched user vocabulary list: {}", userVocabularyList);

        if (userVocabularyList.isEmpty()) {
            log.debug("No matching words found in user's vocabulary");
            messageService.sendMessage(botEvent.getId(), "Такого слова не нашлось в вашем словаре");
        } else {
            for (UserVocabulary userVocabulary : userVocabularyList) {
                var word = userVocabulary.getWord();
                var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.getId().toString());
                log.debug("Sending message with inline keyboard for word: {}", word);
                messageService.sendMessageWithKeyboard(botEvent.getId(), word.toString(), keyboard);
            }
        }
    }


    @Override
    public UserStateEnum availableFor() {
        return UserStateEnum.DELETE_MENU;
    }
}
