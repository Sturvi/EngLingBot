package com.example.englingbot.service.telegrambot.admin.handlers.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
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
public class SomeYesCommandHandler implements SomeAdminCallbackQueryHandler {
    private final WordReviewService wordReviewService;
    private final TelegramMessageService telegramMessageService;
    private final AdminInlineKeyboardMarkupFactory adminInlineKeyboardMarkupFactory;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        if (appUser.getUserState() == UserStateEnum.WORD_REVIEW) {
            Long wordReviewId = KeyboardDataEnum.getWordId(botEvent.getData());

            var wordReviewOpt = wordReviewService.getWordReviewById(wordReviewId);

            if (wordReviewOpt.isPresent()){
                var wordReview = wordReviewOpt.get();
                wordReviewService.deleteWordReview(wordReview);

                var keyboard = adminInlineKeyboardMarkupFactory.getNextKeyboard();
                String newMessageText = "Cлово " + wordReview.getWord() + " принято!";

                telegramMessageService.editMessageWithInlineKeyboard(botEvent, newMessageText, keyboard);
            }
        }
    }


    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.YES;
    }
}
