package com.example.englingbot.service.admin.handlers.textmessagehandler.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.WordReview;
import com.example.englingbot.service.admin.WordReviewService;
import com.example.englingbot.service.admin.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.admin.handlers.textmessagehandler.SomeAdminMessageHandler;
import com.example.englingbot.service.admin.keyboards.AdminReplyKeyboardFactory;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SomeAdminReviewCommand implements SomeAdminMessageHandler {
    private final WordReviewService wordReviewService;
    private final MessageService messageService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var wordReviewOpt = wordReviewService.getWordReview();

        if (wordReviewOpt.isPresent()) {
            var wordReview = wordReviewOpt.get();

            var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard(wordReview.getId().toString());

            String messageText = "Слово для проверки:\n\n" + wordReview.toString();

            messageService.sendMessageWithKeyboard(botEvent.getId(), messageText, keyboard);
        } else {
            var keyboard = AdminReplyKeyboardFactory.getReplyKeyboardMarkup();

            messageService.sendMessageWithKeyboard(botEvent.getId(), "На данный момент нет слов для проверки", keyboard);
        }
    }

    @Override
    public AdminTextComandsEnum availableFor() {
        return AdminTextComandsEnum.REVIEW_NEW_WORD;
    }
}
