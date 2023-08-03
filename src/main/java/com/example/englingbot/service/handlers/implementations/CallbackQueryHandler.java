package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.UserWordListService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.editmessage.EditMessageForUserFactory;
import com.example.englingbot.service.message.sendmessage.SendMessageForUserFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.example.englingbot.model.enums.UserStateEnum.*;

@Component
public class CallbackQueryHandler implements Handler {
    private Map<KeyboardDataEnum, BiConsumer<BotEvent, AppUser>> callbackQueryHandler;
    private final WordService wordService;
    private final SendMessageForUserFactory sendMessageForUser;
    private final EditMessageForUserFactory editMessageForUserFactory;
    private final UserWordListService userWordListService;

    public CallbackQueryHandler(WordService wordService, SendMessageForUserFactory sendMessageForUser, EditMessageForUserFactory editMessageForUserFactory, UserWordListService userWordListService) {
        this.wordService = wordService;
        this.sendMessageForUser = sendMessageForUser;
        this.editMessageForUserFactory = editMessageForUserFactory;
        this.userWordListService = userWordListService;
        callbackQueryHandler = Map.of(
                KeyboardDataEnum.TRANSLATOR, this::handleTranslator,
                KeyboardDataEnum.NO, this::handleNoCommand,
                KeyboardDataEnum.YES, this::handleYesCommand
        );
    }


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var dataEnum = KeyboardDataEnum.fromData(botEvent.getData());
        var handler = callbackQueryHandler.get(dataEnum);

        handler.accept(botEvent, appUser);
    }

    private void handleYesCommand(BotEvent botEvent, AppUser appUser) {
        var userState = appUser.getUserState();

        if (userState == ADD_MENU) {
            var word = wordService.getWordByTextMessage(botEvent.getText());
            userWordListService.addWordToUserWordList(word, appUser);

            String newTextForMessage = "Слово: " + botEvent.getText() + " добавлено в Ваш словарь.";

            editMessageForUserFactory
                    .createNewMessage()
                    .editTextAndDeleteInlineKeyboard(botEvent, newTextForMessage);
        } else if (userState == DELETE_MENU) {

        }

    }

    private void handleNoCommand(BotEvent botEvent, AppUser appUser) {
        editMessageForUserFactory
                .createNewMessage()
                .deleteInlineKeyboard(botEvent);
    }

    private void handleTranslator(BotEvent botEvent, AppUser appUser) {
        String wordString = wordService.getStringBetweenSpaces(botEvent.getText());

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
