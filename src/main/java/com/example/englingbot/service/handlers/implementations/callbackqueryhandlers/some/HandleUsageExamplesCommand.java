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
        log.debug("Handling 'Usage Examples' command for bot event: {}", botEvent);
        var wordText = KeyboardDataEnum.getWord(botEvent.getData());
        var word = wordService.getWordByTextMessage(wordText);

        try {
            if (word.getUsageExamples() == null) {
                wordService.addUsageExamples(word);
            }
            messageService.sendMessage(botEvent.getId(), word.getUsageExamples());
        } catch (NullPointerException e) {
            log.error("Произошла ошибка во время отправки Примеров использования ", e);
            templateMessagesSender.sendErrorMessage(botEvent.getId());
        }
    }

    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.USAGE_EXAMPLES;
    }
}
