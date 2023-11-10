package com.example.englingbot.service.telegrambot.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.telegrambot.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.telegrambot.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleStartCommand implements SomeMessageHandler {
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handle Start Command for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.MAIN);

        templateMessagesSender.sendStartAndHelpMessage(botEvent.getId());

        log.debug("Finished handle Start Command for event: {}", botEvent);
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.START;
    }
}
