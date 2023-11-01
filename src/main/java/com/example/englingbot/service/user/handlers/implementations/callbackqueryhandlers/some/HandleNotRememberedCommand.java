package com.example.englingbot.service.user.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

        userVocabularyOpt.ifPresent(this::updateUserVocabulary);

        telegramMessageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NOT_REMEMBERED;
    }

    private void updateUserVocabulary(UserVocabulary userVocabulary) {
        if (userVocabulary.getFailedAttempts() > 4) {
            userVocabulary.setTimerValue(userVocabulary.getTimerValue() - 1);
            userVocabulary.setFailedAttempts(0);
        } else {
            userVocabulary.setFailedAttempts(userVocabulary.getFailedAttempts() + 1);
            if (userVocabulary.getListType() == UserWordState.LEARNED) {
                userVocabulary.setListType(UserWordState.REPETITION);
            }
        }

        userVocabularyService.save(userVocabulary);
    }

}
