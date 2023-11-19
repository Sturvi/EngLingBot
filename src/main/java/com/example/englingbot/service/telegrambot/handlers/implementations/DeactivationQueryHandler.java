package com.example.englingbot.service.telegrambot.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeactivationQueryHandler implements Handler {
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        appUser.setUserStatus(false);
    }

    @Override
    public boolean canHandle(BotEvent botEvent, AppUser appUser) {
        return botEvent.isDeactivationQuery();
    }
}
