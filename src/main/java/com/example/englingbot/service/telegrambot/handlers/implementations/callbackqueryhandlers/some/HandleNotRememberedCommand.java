package com.example.englingbot.service.telegrambot.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.telegrambot.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.telegrambot.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleNotRememberedCommand implements SomeCallbackQueryHandler {
    private final TelegramMessageService telegramMessageService;
    private final InlineKeyboardMarkupFactory inlineKeyboardMarkupFactory;
    private final UserVocabularyService userVocabularyService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Not Remembered' command for bot event: {}", botEvent);
        var keyboard = inlineKeyboardMarkupFactory.getNextKeyboard();

        var wordId = KeyboardDataEnum.getWordId(botEvent.getData());
        var userVocabularyOpt = userVocabularyService.getUserVocabulary(appUser, wordId);

        if (userVocabularyOpt.isPresent()) {
            var userVocabulary = userVocabularyOpt.get();

            boolean isChanged = updateUserVocabulary(userVocabulary);

            if (isChanged) {
                String messageText =  "Слово " + userVocabulary.getWord() + " понижено в уровне.";
                telegramMessageService.editMessageWithInlineKeyboard(botEvent, messageText, keyboard);
            } else {
                telegramMessageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
            }
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NOT_REMEMBERED;
    }

    private boolean updateUserVocabulary(UserVocabulary userVocabulary) {
        boolean isChanged = false;

        if (userVocabulary.getTimerValue() > 0) {
            if (userVocabulary.getFailedAttempts() > 4) {
                userVocabulary.setTimerValue(userVocabulary.getTimerValue() - 1);
                userVocabulary.setFailedAttempts(0);

                isChanged = true;
            } else {
                if (userVocabulary.getListType() == UserWordState.LEARNED) {
                    userVocabulary.setListType(UserWordState.LEARNING);
                    userVocabulary.setFailedAttempts(1);

                    isChanged = true;
                } else {
                    userVocabulary.setFailedAttempts(userVocabulary.getFailedAttempts() + 1);
                }
            }
        }

        userVocabularyService.save(userVocabulary);
        return isChanged;
    }

}
