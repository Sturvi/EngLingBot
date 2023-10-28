package com.example.englingbot.service.user.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.message.TelegramMessageService;
import com.example.englingbot.service.user.handlers.interfaces.SomeMessageHandler;
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
