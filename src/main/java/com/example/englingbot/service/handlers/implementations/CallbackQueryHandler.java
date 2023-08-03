package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.sendmessage.SendMessageForUserFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class CallbackQueryHandler implements Handler {
    private Map<KeyboardDataEnum, BiConsumer<BotEvent, AppUser>> callbackQueryHandler;
    private final WordService wordService;
    private final SendMessageForUserFactory sendMessageForUser;

    public CallbackQueryHandler(WordService wordService, SendMessageForUserFactory sendMessageForUser) {
        this.wordService = wordService;
        this.sendMessageForUser = sendMessageForUser;
        callbackQueryHandler = Map.of(
                KeyboardDataEnum.TRANSLATOR, this::handleTranslator
        );
    }

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var dataEnum = KeyboardDataEnum.fromData(botEvent.getData());
        var handler = callbackQueryHandler.get(dataEnum);

        handler.accept(botEvent, appUser);
    }

    private void handleTranslator(BotEvent botEvent, AppUser appUser) {
        String wordString = wordService.getWordBetweenSpaces(botEvent.getText());

        var newWordsList = wordService.addNewWordFromExternalApi(wordString);
        var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard();

        for (Word word :
                newWordsList) {
            sendMessageForUser
                    .createNewMessage()
                    .sendMessageWithInlineKeyboard(botEvent.getId(), word.toString(), keyboard);
        }
    }



}
