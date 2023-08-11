package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleRememberedCommand implements SomeCallbackQueryHandler {
    private final WordService wordService;
    private final UserVocabularyService userVocabularyService;
    private final MessageService messageService;
    private final TemplateMessagesSender templateMessagesSender;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Remembered' command for bot event: {}", botEvent);
        var wordId = KeyboardDataEnum.getWordId(botEvent.getData());
        var wordOptional = wordService.getWord(wordId);

        if (wordOptional.isPresent()) {
            var word = wordOptional.get();
            userVocabularyService.updateUserVocabulary(appUser, word);

            var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();

            messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
        } else {
            templateMessagesSender.sendErrorMessage(botEvent.getId());
        }


    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.REMEMBERED;
    }
}
