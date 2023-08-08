package com.example.englingbot.service.handlers.interfaces;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeCallbackQueryHandler {

    void handle (BotEvent botEvent, AppUser appUser);
    KeyboardDataEnum availableFor();
}
