package com.example.englingbot.service.admin.handlers.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.admin.WordReviewService;
import com.example.englingbot.service.admin.handlers.callbackqueryhandlers.SomeAdminCallbackQueryHandler;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SomeYesCommandHandler implements SomeAdminCallbackQueryHandler {
    private final WordReviewService wordReviewService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        if (appUser.getUserState() == UserStateEnum.WORD_REVIEW) {
            Long wordReviewId = KeyboardDataEnum.getWordId(botEvent.getData());

            var wordReviewOpt = wordReviewService.getWordReviewById(wordReviewId);

            wordReviewOpt.ifPresent(wordReviewService::deleteWordReview);
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.YES;
    }
}
