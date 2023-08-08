package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleContextCommand implements SomeCallbackQueryHandler {
    private final WordService wordService;
    private final MessageService messageService;
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Context' command for bot event: {}", botEvent);
        var wordText = KeyboardDataEnum.getWord(botEvent.getData());
        var word = wordService.getWordByTextMessage(wordText);

        try {
            if (word.getContext() == null) {
                wordService.addWordContext(word);
            }
            messageService.sendMessage(botEvent.getId(), word.getContext());
        } catch (NullPointerException e) {
            log.error("Произошла ошибка во время отправки контекста ", e);
            templateMessagesSender.sendErrorMessage(botEvent.getId());
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.CONTEXT;
    }
}
