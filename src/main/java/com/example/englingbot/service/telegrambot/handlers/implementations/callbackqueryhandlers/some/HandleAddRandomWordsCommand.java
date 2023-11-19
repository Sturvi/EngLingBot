package com.example.englingbot.service.telegrambot.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.telegrambot.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.telegrambot.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.telegrambot.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HandleAddRandomWordsCommand implements SomeCallbackQueryHandler {
    private final WordService wordService;
    private final TelegramMessageService telegramMessageService;
    private final InlineKeyboardMarkupFactory inlineKeyboardMarkupFactory;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        appUser.setUserState(UserStateEnum.ADD_MENU);
        var newWordList = wordService.getTenRandomNewWord(appUser);

        for (Word word :
                newWordList) {
            var keyboard = inlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.getId().toString());
            telegramMessageService.sendMessageWithKeyboard(botEvent.getId(), word.toString(), keyboard);
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.ADD_RANDOM_WORDS;
    }

}
