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

/**
 * Handler class responsible for managing the callback queries.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryHandler implements Handler {
    private Map<KeyboardDataEnum, BiConsumer<BotEvent, AppUser>> callbackQueryHandlers;
    private final WordService wordService;
    private final MessageService messageService;
    private final TemplateMessagesSender templateMessagesSender;
    private final UserVocabularyService userVocabularyService;

    /**
     * Initializes callback query handlers.
     */
    @PostConstruct
    private void init() {
        log.debug("Initializing CallbackQueryHandler");
        // TODO: Я бы сделал отдельный класс с реализацией на каждый обработчик, те
        //  interface SomeHandler {
        //    AppUser handle(botEvent);
        //    KeyboardDataEnum availableFor()
        //  }
        //
        //  class SomeHandlerImpl {
        //    AppUser handle (botEvent) {
        //      ...
        //    }
        //    KeyboardDataEnum availableFor() {
        //      return KeyboardDataEnum.TRANSLATOR
        //    }
        //  }
        //
        //  А в текущем классе:
        //  class CallbackQueryHandler {
        //    Map<...> callbackQueryHandlers;
        //    @PostConstruct
        //    void init(List<SomeHandler> handlers) {
        //      callbackQueryHandlers = handlers.stream().collect(Collectors.toMap(
        //        element -> element.availableFor(),
        //        element -> element
        //      ))
        //    }
        //  }

        callbackQueryHandlers = Map.of(
                KeyboardDataEnum.TRANSLATOR, this::handleTranslator,
                KeyboardDataEnum.NO, this::handleNoCommand,
                KeyboardDataEnum.YES, this::handleYesCommand,
                KeyboardDataEnum.NOT_REMEMBERED, this::handleNotRemembered,
                KeyboardDataEnum.REMEMBERED, this::handleRemembered,
                KeyboardDataEnum.CONTEXT, this::handleContext,
                KeyboardDataEnum.USAGE_EXAMPLES, this::handleUsageExamples,
                KeyboardDataEnum.LEARNED, this::handleLearned,
                KeyboardDataEnum.NEXT, this::handleNext
        );
    }

    /**
     * Handles a given BotEvent and AppUser.
     *
     * @param botEvent The bot event to be handled.
     * @param appUser  The associated app user.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling bot event: {}", botEvent);
        var dataEnum = KeyboardDataEnum.fromData(botEvent.getData());
        var handler = callbackQueryHandlers.get(dataEnum);

        handler.accept(botEvent, appUser);
    }

    /**
     * Handles the "Next" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleNext(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Next' command for bot event: {}", botEvent);
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

    /**
     * Handles the "Learned" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleLearned(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Learned' command for bot event: {}", botEvent);
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

    /**
     * Handles the "Usage Examples" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleUsageExamples(BotEvent botEvent, AppUser appUser) {
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

    /**
     * Handles the "Context" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleContext(BotEvent botEvent, AppUser appUser) {
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

    /**
     * Handles the "Remembered" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleRemembered(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Remembered' command for bot event: {}", botEvent);
        var wordText = KeyboardDataEnum.getWord(botEvent.getData());
        var word = wordService.getWordByTextMessage(wordText);

        userVocabularyService.updateUserVocabulary(appUser, word);

        var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();

        messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
    }

    /**
     * Handles the "Not Remembered" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleNotRemembered(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Not Remembered' command for bot event: {}", botEvent);
        var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();

        messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
    }

    /**
     * Handles the "Yes" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleYesCommand(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Yes' command for bot event: {}", botEvent);
        var userState = appUser.getUserState();

        if (userState == ADD_MENU) {
            var wordText = KeyboardDataEnum.getWord(botEvent.getData());
            var word = wordService.getWordByTextMessage(wordText);
            userVocabularyService.addWordToUserVocabulary(word, appUser);

            String newTextForMessage = "Слово: " + botEvent.getText() + " добавлено в Ваш словарь.";

            messageService
                    .editTextAndDeleteInlineKeyboard(botEvent, newTextForMessage);
        } else if (userState == DELETE_MENU) {
            // Handle delete menu logic here
        }
    }

    /**
     * Handles the "No" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleNoCommand(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'No' command for bot event: {}", botEvent);
        messageService.deleteInlineKeyboard(botEvent);
    }

    /**
     * Handles the "Translator" command.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleTranslator(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Translator' command for bot event: {}", botEvent);
        String wordString = wordService.getStringBetweenSpaces(botEvent.getText());

        var newWordsList = wordService.addNewWordFromExternalApi(wordString);

        for (Word word : newWordsList) {
            var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.toString());
            messageService.sendMessageWithInlineKeyboard(botEvent.getId(), word.toString(), keyboard);
        }
    }
}
