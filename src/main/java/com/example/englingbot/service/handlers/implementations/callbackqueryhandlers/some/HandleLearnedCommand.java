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
        log.trace("Starting handle method for BotEvent with ID: {}", botEvent.getId());

        var wordId = KeyboardDataEnum.getWordId(botEvent.getData());
        log.debug("Retrieved wordId: {}", wordId);

        var wordOptional = wordService.getWord(wordId);

        if (wordOptional.isPresent()) {
            var word = wordOptional.get();
            log.debug("Word found: {}", word);

            userVocabularyService.setLearnedState(appUser, word);
            log.debug("Set learned state for user: {}", appUser.getId());

            var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();
            messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
            log.debug("Edited message with inline keyboard for BotEvent with ID: {}", botEvent.getId());
        } else {
            log.warn("Word not found for wordId: {}", wordId);
            templateMessagesSender.sendErrorMessage(botEvent.getId());
        }

        log.trace("Finished handle method for BotEvent with ID: {}", botEvent.getId());
    }


    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.LEARNED;
    }
}
