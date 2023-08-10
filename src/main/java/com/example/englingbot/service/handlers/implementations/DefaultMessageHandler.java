package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.Handler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Handler class responsible for handling default messages.
 */
@Component
@Slf4j
public class DefaultMessageHandler implements Handler {
    private final Map<UserStateEnum, BiConsumer<BotEvent, AppUser>> userStateHandlers;
    private final WordService wordService;
    private final MessageService messageService;

    public DefaultMessageHandler(WordService wordService, MessageService messageService) {
        log.debug("Initializing DefaultMessageHandler");
        this.wordService = wordService;
        this.messageService = messageService;
        userStateHandlers = Map.of(
                UserStateEnum.ADD_MENU, this::handleAddMenu
        );
    }

    /**
     * Handles the given BotEvent and AppUser based on the user's state.
     *
     * @param botEvent The bot event to be handled.
     * @param appUser  The associated app user.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling bot event: {}", botEvent);
        userStateHandlers
                .get(appUser.getUserState())
                .accept(botEvent, appUser);
    }

    /**
     * Handles the "Add Menu" command by processing the incoming word.
     *
     * @param botEvent The bot event containing the command.
     * @param appUser  The associated app user.
     */
    private void handleAddMenu(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Add Menu' command for bot event: {}", botEvent);
        String incomingWord = botEvent.getText();

        var wordList = wordService.fetchWordList(incomingWord);

        if (!wordList.isEmpty()) {
            for (Word word : wordList) {
                var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.toString());
                messageService
                        .sendMessageWithInlineKeyboard(botEvent.getId(), word.toString(), keyboard);
            }

            var keyboard = InlineKeyboardMarkupFactory.getWordFromTranslatorKeyboard(incomingWord);
            messageService.sendMessageWithInlineKeyboard(botEvent.getId(),
                    "Нет нужного перевода?",
                    keyboard);
        } else {
            var keyboard = InlineKeyboardMarkupFactory.getWordFromTranslatorKeyboard(incomingWord);
            messageService
                    .sendMessageWithInlineKeyboard(
                            botEvent.getId(),
                            "К сожалению у нас в базе не нашлось слова '" + botEvent.getText() + "'.",
                            keyboard);
        }
    }
}
