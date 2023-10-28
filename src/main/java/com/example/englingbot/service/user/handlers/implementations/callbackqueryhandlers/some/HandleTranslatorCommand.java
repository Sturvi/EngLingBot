package com.example.englingbot.service.user.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleTranslatorCommand implements SomeCallbackQueryHandler {

    private final WordService wordService;
    private final TelegramMessageService telegramMessageService;
    private final InlineKeyboardMarkupFactory inlineKeyboardMarkupFactory;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Translator' command for bot event: {}", botEvent);
        String wordString = KeyboardDataEnum.getWord(botEvent.getData());

        var newWordsList = wordService.addNewWordFromExternalApi(wordString);

        if (newWordsList.isEmpty()){
            telegramMessageService.sendMessageToUser(botEvent.getId(), "Не получено новых переводов слов.");
            return;
        }

        for (Word word : newWordsList) {
            var keyboard = inlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.getId().toString());
            telegramMessageService.sendMessageWithKeyboard(botEvent.getId(), word.toString(), keyboard);
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.TRANSLATOR;
    }
}
