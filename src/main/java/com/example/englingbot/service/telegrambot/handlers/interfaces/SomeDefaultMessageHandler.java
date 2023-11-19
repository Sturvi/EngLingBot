package com.example.englingbot.service.telegrambot.handlers.interfaces;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeDefaultMessageHandler {

    void handle(BotEvent botEvent, AppUser appUser);

    UserStateEnum availableFor();
}
