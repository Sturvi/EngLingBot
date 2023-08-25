package com.example.englingbot.service.user.handlers.interfaces;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

public interface SomeMessageHandler {

    void handle(BotEvent botEvent, AppUser appUser);

    UserTextCommandsEnum availableFor();
}
