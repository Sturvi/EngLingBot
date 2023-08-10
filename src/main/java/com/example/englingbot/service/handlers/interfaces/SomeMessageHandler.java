package com.example.englingbot.service.handlers.interfaces;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeMessageHandler {

    void handle(BotEvent botEvent, AppUser appUser);

    TextCommandsEnum availableFor();
}
