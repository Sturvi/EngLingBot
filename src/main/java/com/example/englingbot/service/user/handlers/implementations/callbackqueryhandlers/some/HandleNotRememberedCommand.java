package com.example.englingbot.service.user.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
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
public class HandleNotRememberedCommand implements SomeCallbackQueryHandler {
    private final TelegramMessageService telegramMessageService;
    private final InlineKeyboardMarkupFactory inlineKeyboardMarkupFactory;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Not Remembered' command for bot event: {}", botEvent);
        var keyboard = inlineKeyboardMarkupFactory.getNextKeyboard();

        telegramMessageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NOT_REMEMBERED;
    }
}
