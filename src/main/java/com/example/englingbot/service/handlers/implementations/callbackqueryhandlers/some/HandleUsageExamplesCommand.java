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
public class HandleUsageExamplesCommand implements SomeCallbackQueryHandler {
    private final WordService wordService;
    private final MessageService messageService;
    private final TemplateMessagesSender templateMessagesSender;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Handling event for user: {}", appUser.getUsername());

        var wordId = KeyboardDataEnum.getWordId(botEvent.getData());
        log.debug("Retrieved wordId: {}", wordId);

        var wordOptional = wordService.getWord(wordId);

        if (wordOptional.isPresent()) {
            var word = wordOptional.get();
            log.debug("Found word: {}", word);

            if (word.getUsageExamples() == null) {
                log.debug("Usage examples not found for word: {}. Adding...", word);
                wordService.addUsageExamples(word);
            }

            messageService.sendMessageToUser(botEvent.getId(), word.getUsageExamples());
        } else {
            log.warn("Word not found for text: {}. Sending error message.", wordId);
            templateMessagesSender.sendErrorMessage(botEvent.getId());
        }
    }


    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.USAGE_EXAMPLES;
    }
}
