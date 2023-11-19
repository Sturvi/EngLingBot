package com.example.englingbot.service.telegrambot.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.telegrambot.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleNoCommand implements SomeCallbackQueryHandler {
    private final TelegramMessageService telegramMessageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'No' command for bot event: {}", botEvent);
        telegramMessageService.deleteInlineKeyboard(botEvent);
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NO;
    }
}
