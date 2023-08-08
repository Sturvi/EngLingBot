package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleNoCommand implements SomeCallbackQueryHandler {
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'No' command for bot event: {}", botEvent);
        messageService.deleteInlineKeyboard(botEvent);
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NO;
    }
}
