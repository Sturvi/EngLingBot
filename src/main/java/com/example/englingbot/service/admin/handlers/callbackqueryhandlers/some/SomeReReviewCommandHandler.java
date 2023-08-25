package com.example.englingbot.service.admin.handlers.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.admin.WordReviewService;
import com.example.englingbot.service.admin.handlers.callbackqueryhandlers.SomeAdminCallbackQueryHandler;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.AdminInlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
@RequiredArgsConstructor
public class SomeReReviewCommandHandler implements SomeAdminCallbackQueryHandler {
    private final WordReviewService wordReviewService;
    private final AdminInlineKeyboardMarkupFactory adminInlineKeyboardMarkupFactory;
    private final MessageService messageService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var id = KeyboardDataEnum.getWordId(botEvent.getData());

        var wordReviewOpt = wordReviewService.getWordReviewById(id);

        if (wordReviewOpt.isPresent()) {
            var wordReview = wordReviewOpt.get();
            wordReviewService.resendWordToReview(wordReview);

            var keyboard = adminInlineKeyboardMarkupFactory.getYesNoAndReReviewKeyboard(wordReview.getId().toString());

            String messageText = "Слово для проверки:\n\n" + wordReview;

            messageService.sendMessageWithKeyboard(botEvent.getId(), messageText, keyboard);
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.RE_REVIEW;
    }
}
