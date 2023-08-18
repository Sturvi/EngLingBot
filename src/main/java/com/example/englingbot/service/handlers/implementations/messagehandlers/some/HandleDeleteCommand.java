package com.example.englingbot.service.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HandleDeleteCommand implements SomeMessageHandler {
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Entering handle method");
        appUser.setUserState(UserStateEnum.DELETE_MENU);

        messageService.sendMessageToUser(botEvent.getId(),
                "Отправьте в виде сообщения слово, которое вы хотите удалить из вашего словаря!");

        log.trace("Exiting handle method");
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.DELETE;
    }
}
