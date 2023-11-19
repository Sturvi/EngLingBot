package com.example.englingbot.service.telegrambot.admin.handlers.textmessagehandler;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.telegrambot.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeAdminMessageHandler {
    void handle(BotEvent botEvent, AppUser appUser);

    AdminTextComandsEnum availableFor();
}
