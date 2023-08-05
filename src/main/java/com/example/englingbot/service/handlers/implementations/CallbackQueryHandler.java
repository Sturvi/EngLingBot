package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.example.englingbot.model.enums.UserStateEnum.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryHandler implements Handler {
    private Map<KeyboardDataEnum, BiConsumer<BotEvent, AppUser>> callbackQueryHandlers;
    private final WordService wordService;
    private final MessageService messageService;
    private final TemplateMessagesSender templateMessagesSender;
    private final UserVocabularyService userVocabularyService;

    @PostConstruct
    private void init() {
        callbackQueryHandlers = Map.of(
                KeyboardDataEnum.TRANSLATOR, this::handleTranslator,
                KeyboardDataEnum.NO, this::handleNoCommand,
                KeyboardDataEnum.YES, this::handleYesCommand,
                KeyboardDataEnum.NOTREMEMBERED, this::handleNotRemembered,
                KeyboardDataEnum.REMEMBERED, this::handleRemembered,
                KeyboardDataEnum.CONTEXT, this::handleContext,
                KeyboardDataEnum.USAGEEXAMPLES, this::handleUsageExamples,
                KeyboardDataEnum.LEARNED, this::handleLearned,
                KeyboardDataEnum.NEXT, this::handleNext
        );
    }

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var dataEnum = KeyboardDataEnum.fromData(botEvent.getData());
        var handler = callbackQueryHandlers.get(dataEnum);

        handler.accept(botEvent, appUser);
    }

    private void handleNext(BotEvent botEvent, AppUser appUser) {
        var userState = appUser.getUserState();

        switch (userState) {
            case LEARNING -> userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING);
            case REPETITION ->
                    userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.REPETITION);
            case MIXED ->
                    userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING, UserWordState.REPETITION);
            default ->
                    messageService.sendMessage(botEvent.getId(), "Для изучения слов выберите пожалуйста соответствующее меню");
        }
    }

    private void handleLearned(BotEvent botEvent, AppUser appUser) {
        var wordText = KeyboardDataEnum.getWord(botEvent.getData());
        var word = wordService.getWordByTextMessage(wordText);

        try {
            userVocabularyService.setLearnedState(appUser, word);
            var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();
            messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
        } catch (NullPointerException e) {
            log.error("Ошибка обработки запроса ", e);
            templateMessagesSender.sendErrorMessage(botEvent.getId());
        }
    }

    private void handleUsageExamples(BotEvent botEvent, AppUser appUser) {
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

    private void handleContext(BotEvent botEvent, AppUser appUser) {
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

    private void handleRemembered(BotEvent botEvent, AppUser appUser) {
        var wordText = KeyboardDataEnum.getWord(botEvent.getData());
        var word = wordService.getWordByTextMessage(wordText);

        userVocabularyService.updateUserVocabulary(appUser, word);

        var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();

        messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
    }

    private void handleNotRemembered(BotEvent botEvent, AppUser appUser) {
        var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();

        messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
    }


    private void handleYesCommand(BotEvent botEvent, AppUser appUser) {
        var userState = appUser.getUserState();

        if (userState == ADD_MENU) {
            var wordText = KeyboardDataEnum.getWord(botEvent.getData());
            var word = wordService.getWordByTextMessage(wordText);
            userVocabularyService.addWordToUserVocabulary(word, appUser);

            String newTextForMessage = "Слово: " + botEvent.getText() + " добавлено в Ваш словарь.";

            messageService
                    .editTextAndDeleteInlineKeyboard(botEvent, newTextForMessage);
        } else if (userState == DELETE_MENU) {

        }
    }

    private void handleNoCommand(BotEvent botEvent, AppUser appUser) {
        messageService.deleteInlineKeyboard(botEvent);
    }

    private void handleTranslator(BotEvent botEvent, AppUser appUser) {
        String wordString = wordService.getStringBetweenSpaces(botEvent.getText());

        var newWordsList = wordService.addNewWordFromExternalApi(wordString);

        for (Word word : newWordsList) {
            var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.toString());
            messageService.sendMessageWithInlineKeyboard(botEvent.getId(), word.toString(), keyboard);
        }
    }
}
