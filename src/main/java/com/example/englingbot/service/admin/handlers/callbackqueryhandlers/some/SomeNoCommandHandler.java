package com.example.englingbot.service.admin.handlers.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.admin.WordReviewService;
import com.example.englingbot.service.admin.handlers.callbackqueryhandlers.SomeAdminCallbackQueryHandler;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
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


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        if (appUser.getUserState() == UserStateEnum.WORD_REVIEW) {
            Long wordReviewId = KeyboardDataEnum.getWordId(botEvent.getData());

            var wordReviewOpt = wordReviewService.getWordReviewById(wordReviewId);

            wordReviewOpt.ifPresent(w -> {
                var appUserList = userVocabularyService.getAppUserListByWord(w.getWord());
                appUserList.forEach(user -> messageService.sendMessage(
                        user.getTelegramChatId(),
                        "К сожалению в слове " + w.getWord() + " была найдена ошибка и она удалена."));
                wordService.deleteWord(w.getWord());
            });
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NO;
    }
}
