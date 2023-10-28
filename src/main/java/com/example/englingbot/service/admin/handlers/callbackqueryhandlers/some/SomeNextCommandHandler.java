package com.example.englingbot.service.admin.handlers.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.admin.WordReviewService;
import com.example.englingbot.service.admin.handlers.callbackqueryhandlers.SomeAdminCallbackQueryHandler;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.AdminInlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SomeNextCommandHandler implements SomeAdminCallbackQueryHandler {
    private final WordReviewService wordReviewService;
    private final TelegramMessageService telegramMessageService;
    private final AdminInlineKeyboardMarkupFactory adminInlineKeyboardMarkupFactory;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var wordReviewOpt = wordReviewService.getWordReview();

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
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NEXT;
    }
}
