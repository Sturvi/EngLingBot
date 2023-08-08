package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleNotRememberedCommand implements SomeCallbackQueryHandler {
    private final MessageService messageService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Not Remembered' command for bot event: {}", botEvent);
        var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard();

        messageService.editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboard);
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NOT_REMEMBERED;
    }
}
