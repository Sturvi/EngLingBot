package com.example.englingbot.service.telegrambot.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.telegrambot.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleHomeCommand implements SomeMessageHandler {
    private final TelegramMessageService telegramMessageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        appUser.setUserState(UserStateEnum.MAIN);
        telegramMessageService.sendMessageToUser(botEvent.getId(), "Выберите меню:");
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.HOME;
    }
}
