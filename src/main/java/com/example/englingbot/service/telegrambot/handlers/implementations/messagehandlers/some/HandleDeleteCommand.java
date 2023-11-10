package com.example.englingbot.service.telegrambot.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.telegrambot.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HandleDeleteCommand implements SomeMessageHandler {
    private final TelegramMessageService telegramMessageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Entering handle method");
        appUser.setUserState(UserStateEnum.DELETE_MENU);

        telegramMessageService.sendMessageToUser(botEvent.getId(),
                "Отправьте в виде сообщения слово, которое вы хотите удалить из вашего словаря!");

        log.trace("Exiting handle method");
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.DELETE;
    }
}
