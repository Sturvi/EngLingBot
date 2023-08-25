package com.example.englingbot.service.user.handlers.implementations.messagehandlers.defaultmessagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeDefaultMessageHandler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleAddMenu implements SomeDefaultMessageHandler {
    private final WordService wordService;
    private final MessageService messageService;
    private final InlineKeyboardMarkupFactory inlineKeyboardMarkupFactory;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Handling bot event: {}", botEvent);
        String incomingWord = botEvent.getText();

        var wordList = wordService.fetchWordList(incomingWord);

        if (!wordList.isEmpty()) {
            for (Word word : wordList) {
                var keyboard = inlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.getId().toString());
                messageService
                        .sendMessageWithKeyboard(botEvent.getId(), word.toString(), keyboard);
            }

            var keyboard = inlineKeyboardMarkupFactory.getWordFromTranslatorKeyboard(incomingWord);
            messageService.sendMessageWithKeyboard(botEvent.getId(),
                    "Нет нужного перевода?",
                    keyboard);
        } else {
            var keyboard = inlineKeyboardMarkupFactory.getWordFromTranslatorKeyboard(incomingWord);
            messageService
                    .sendMessageWithKeyboard(
                            botEvent.getId(),
                            "К сожалению у нас в базе не нашлось слова '" + botEvent.getText() + "'.",
                            keyboard);
        }
    }

    @Override
    public UserStateEnum availableFor() {
        return UserStateEnum.ADD_MENU;
    }
}
