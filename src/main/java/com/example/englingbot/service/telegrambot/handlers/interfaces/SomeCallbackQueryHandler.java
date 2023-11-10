package com.example.englingbot.service.telegrambot.handlers.interfaces;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.telegrambot.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeCallbackQueryHandler {

    void handle (BotEvent botEvent, AppUser appUser);
    KeyboardDataEnum availableFor();
}
