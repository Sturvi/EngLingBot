package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import org.springframework.stereotype.Component;

@Component
public class CallbackQueryHandler implements Handler {
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {

    }
}
