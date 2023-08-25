package com.example.englingbot.service.user.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleAddWordCommand implements SomeMessageHandler {
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handle AddWord command for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ADD_MENU);

        templateMessagesSender.sendAddWordMessage(botEvent.getId());

        log.debug("Finished handle AddWord command for event: {}", botEvent);
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.ADD_WORD;
    }
}
