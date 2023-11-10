package com.example.englingbot.service.telegrambot.admin.handlers.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
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
public class SomeReReviewCommandHandler implements SomeAdminCallbackQueryHandler {
    private final WordReviewService wordReviewService;
    private final AdminInlineKeyboardMarkupFactory adminInlineKeyboardMarkupFactory;
    private final TelegramMessageService telegramMessageService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var id = KeyboardDataEnum.getWordId(botEvent.getData());

        var wordReviewOpt = wordReviewService.getWordReviewById(id);

        if (wordReviewOpt.isPresent()) {
            var wordReview = wordReviewOpt.get();
            wordReviewService.resendWordToReview(wordReview);

            var keyboard = adminInlineKeyboardMarkupFactory.getYesNoAndReReviewKeyboard(wordReview.getId().toString());

            String messageText = "Слово для проверки:\n\n" + wordReview;

            telegramMessageService.sendMessageWithKeyboard(botEvent.getId(), messageText, keyboard);
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.RE_REVIEW;
    }
}
