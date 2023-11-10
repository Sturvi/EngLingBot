package com.example.englingbot.service.telegrambot.admin.handlers.callbackqueryhandlers;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.telegrambot.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeAdminCallbackQueryHandler {

    void handle (BotEvent botEvent, AppUser appUser);
    KeyboardDataEnum availableFor();
}
