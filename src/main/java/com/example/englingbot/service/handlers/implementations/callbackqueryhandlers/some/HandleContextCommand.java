package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
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

    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Handling 'Context' command for bot event: {}", botEvent);
        var wordId = KeyboardDataEnum.getWordId(botEvent.getData());
        var wordOptional = wordService.getWord(wordId);

        if (wordOptional.isPresent()) {
            Word word = wordOptional.get();

            if (word.getContext() == null) {
                wordService.addWordContext(word);
            }
            messageService.sendMessageToUser(botEvent.getId(), word.getContext());
        } else {
            log.error("Failed to find the word '{}' in the database", wordId);
            templateMessagesSender.sendErrorMessage(botEvent.getId());
        }
    }


    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.CONTEXT;
    }
}
