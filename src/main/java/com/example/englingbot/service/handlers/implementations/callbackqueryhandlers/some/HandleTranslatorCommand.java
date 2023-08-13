package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleTranslatorCommand implements SomeCallbackQueryHandler {

    private final WordService wordService;
    private final MessageService messageService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Translator' command for bot event: {}", botEvent);
        String wordString = KeyboardDataEnum.getWord(botEvent.getData());

        var newWordsList = wordService.addNewWordFromExternalApi(wordString);

        if (newWordsList.isEmpty()){
            messageService.sendMessage(botEvent.getId(), "Не получено новых переводов слов.");
            return;
        }

        for (Word word : newWordsList) {
            var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.getId().toString());
            messageService.sendMessageWithKeyboard(botEvent.getId(), word.toString(), keyboard);
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.TRANSLATOR;
    }
}
