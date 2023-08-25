package com.example.englingbot.service.admin.handlers.textmessagehandler;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeAdminMessageHandler {
    void handle(BotEvent botEvent, AppUser appUser);

    AdminTextComandsEnum availableFor();
}
