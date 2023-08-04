package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.sendtextmessage.SendMessageForUserFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class DefaultMessageHandler implements Handler {
    private final Map<UserStateEnum, BiConsumer<BotEvent, AppUser>> userStateHandlers;
    private final WordService wordService;
    private final MessageService messageService;

    public DefaultMessageHandler(WordService wordService, MessageService messageService) {
        this.wordService = wordService;
        this.messageService = messageService;
        userStateHandlers = Map.of(
                UserStateEnum.ADD_MENU, this::handleAddMenu
        );
    }

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        userStateHandlers
                .get(appUser.getUserState())
                .accept(botEvent, appUser);

    }

    private void handleAddMenu(BotEvent botEvent, AppUser appUser) {
        String incomingWord = botEvent.getText();

        var wordList = wordService.fetchWordList(incomingWord);

        if (!wordList.isEmpty()){
            var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard();
            for (Word word :
                    wordList) {
                messageService
                        .sendMessageWithInlineKeyboard(botEvent.getId(), word.toString(), keyboard);
            }
        } else {
            var keyboard = InlineKeyboardMarkupFactory.getWordFromTranslatorKeyboard();
            messageService
                    .sendMessageWithInlineKeyboard(
                            botEvent.getId(),
                            "К сожалению у нас в базе не нашлось слова '" + botEvent.getText() + "'.",
                            keyboard);
        }
    }
}
