package com.example.englingbot.service.telegrambot.admin.handlers.textmessagehandler.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.telegrambot.admin.WordReviewService;
import com.example.englingbot.service.telegrambot.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.telegrambot.admin.handlers.textmessagehandler.SomeAdminMessageHandler;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.keyboards.AdminInlineKeyboardMarkupFactory;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SomeAdminReviewCommand implements SomeAdminMessageHandler {
    private final WordReviewService wordReviewService;
    private final TelegramMessageService telegramMessageService;
    private final AdminInlineKeyboardMarkupFactory adminInlineKeyboardMarkupFactory;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var wordReviewOpt = wordReviewService.getWordReview();
        appUser.setUserState(UserStateEnum.WORD_REVIEW);

        if (wordReviewOpt.isPresent()) {
            var wordReview = wordReviewOpt.get();

            var keyboard = adminInlineKeyboardMarkupFactory.getYesNoAndReReviewKeyboard(wordReview.getId().toString());

            String messageText = "Слово для проверки:\n\n" + wordReview;

            telegramMessageService.sendMessageWithKeyboard(botEvent.getId(), messageText, keyboard);
        } else {
            telegramMessageService.sendMessageToAdmin(botEvent.getId(), "На данный момент нет слов для проверки");
        }
    }

    @Override
    public AdminTextComandsEnum availableFor() {
        return AdminTextComandsEnum.REVIEW_NEW_WORD;
    }
}
