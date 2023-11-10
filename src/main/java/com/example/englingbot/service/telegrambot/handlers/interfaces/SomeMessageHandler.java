package com.example.englingbot.service.telegrambot.handlers.interfaces;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.telegrambot.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeMessageHandler {

    void handle(BotEvent botEvent, AppUser appUser);

    UserTextCommandsEnum availableFor();
}
