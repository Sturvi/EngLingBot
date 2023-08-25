package com.example.englingbot.service.admin.handlers.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.admin.WordReviewService;
import com.example.englingbot.service.admin.handlers.callbackqueryhandlers.SomeAdminCallbackQueryHandler;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.AdminInlineKeyboardMarkupFactory;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
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
    private final MessageService messageService;
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
        appUserList.forEach(user -> messageService.sendMessageToUser(user.getTelegramChatId(), errorMessage));
    }

    private void sendUpdateMessage(BotEvent botEvent, Word word) {
        String newMessageText = "Слово " + word + " удалено";
        var keyboard = adminInlineKeyboardMarkupFactory.getNextKeyboard();
        messageService.editMessageWithInlineKeyboard(botEvent, newMessageText, keyboard);
    }


    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NO;
    }

}
