package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.example.englingbot.model.enums.UserStateEnum.*;

@Component
public class CallbackQueryHandler implements Handler {
    private Map<KeyboardDataEnum, BiConsumer<BotEvent, AppUser>> callbackQueryHandler;
    private final WordService wordService;
    private final MessageService messageService;
    private final UserVocabularyService userVocabularyService;

    public CallbackQueryHandler(WordService wordService, MessageService messageService, UserVocabularyService userVocabularyService) {
        this.wordService = wordService;
        this.messageService = messageService;
        this.userVocabularyService = userVocabularyService;
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
            userVocabularyService.addWordToUserVocabulary(word, appUser);

            String newTextForMessage = "Слово: " + botEvent.getText() + " добавлено в Ваш словарь.";

            messageService
                    .editTextAndDeleteInlineKeyboard(botEvent, newTextForMessage);
        } else if (userState == DELETE_MENU) {

        }

    }

    private void handleNoCommand(BotEvent botEvent, AppUser appUser) {
        messageService
                .deleteInlineKeyboard(botEvent);
    }

    private void handleTranslator(BotEvent botEvent, AppUser appUser) {
        String wordString = wordService.getStringBetweenSpaces(botEvent.getText());

        var newWordsList = wordService.addNewWordFromExternalApi(wordString);
        var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard();

        for (Word word :
                newWordsList) {
            messageService
                    .sendMessageWithInlineKeyboard(botEvent.getId(), word.toString(), keyboard);
        }
    }


}
