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
public class HandleLearnedCommand implements SomeCallbackQueryHandler {
    private final WordService wordService;
    private final UserVocabularyService userVocabularyService;
    private final MessageService messageService;
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Learned' command for bot event: {}", botEvent);
        var wordText = KeyboardDataEnum.getWord(botEvent.getData());
        var word = wordService.getWordByTextMessage(wordText);

        try {
            userVocabularyService.setLearnedState(appUser, word);
            var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();
            messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
        } catch (NullPointerException e) {
            log.error("Ошибка обработки запроса ", e);
            templateMessagesSender.sendErrorMessage(botEvent.getId());
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.LEARNED;
    }
}
