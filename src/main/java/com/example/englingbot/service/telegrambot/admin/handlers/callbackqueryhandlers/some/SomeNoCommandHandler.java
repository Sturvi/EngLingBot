package com.example.englingbot.service.telegrambot.admin.handlers.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.telegrambot.admin.WordReviewService;
import com.example.englingbot.service.telegrambot.admin.handlers.callbackqueryhandlers.SomeAdminCallbackQueryHandler;
import com.example.englingbot.service.telegrambot.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.keyboards.AdminInlineKeyboardMarkupFactory;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SomeNoCommandHandler implements SomeAdminCallbackQueryHandler {
    private final WordReviewService wordReviewService;
    private final WordService wordService;
    private final UserVocabularyService userVocabularyService;
    private final TelegramMessageService telegramMessageService;
    private final AdminInlineKeyboardMarkupFactory adminInlineKeyboardMarkupFactory;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        if (appUser.getUserState() == UserStateEnum.WORD_REVIEW) {
            Long wordReviewId = KeyboardDataEnum.getWordId(botEvent.getData());
            var wordReviewOpt = wordReviewService.getWordReviewById(wordReviewId);

            if (wordReviewOpt.isEmpty()) {
                return;
            }

            var wordReview = wordReviewOpt.get();
            notifyUsersAboutWordError(wordReview.getWord());
            wordService.deleteWord(wordReview.getWord());
            sendUpdateMessage(botEvent, wordReview.getWord());
        }
    }

    private void notifyUsersAboutWordError(Word word) {
        var appUserList = userVocabularyService.getAppUserListByWord(word);
        String errorMessage = "К сожалению в слове " + word + " была найдена ошибка и она удалена.";
        appUserList.forEach(user -> telegramMessageService.sendMessageToUser(user.getTelegramChatId(), errorMessage));
    }

    private void sendUpdateMessage(BotEvent botEvent, Word word) {
        String newMessageText = "Слово " + word + " удалено";
        var keyboard = adminInlineKeyboardMarkupFactory.getNextKeyboard();
        telegramMessageService.editMessageWithInlineKeyboard(botEvent, newMessageText, keyboard);
    }


    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NO;
    }

}
