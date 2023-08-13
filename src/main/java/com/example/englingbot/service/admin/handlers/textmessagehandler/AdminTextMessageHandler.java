package com.example.englingbot.service.admin.handlers.textmessagehandler;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.service.admin.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.Handler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminTextMessageHandler implements Handler {
    private final List<SomeAdminMessageHandler> handlers;
    private Map<AdminTextComandsEnum, BiConsumer<BotEvent, AppUser>> adminTextCommandsHandler;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        AdminTextComandsEnum incomingCommand = AdminTextComandsEnum.fromString(botEvent.getText());

        var handlerMethod = adminTextCommandsHandler.get(incomingCommand);

        handlerMethod.accept(botEvent, appUser);
    }

    @Override
    public boolean canHandle(BotEvent botEvent, AppUser appUser) {
        return appUser.getRole() == UserRoleEnum.ADMIN &&
                botEvent.isMessage() &&
                !botEvent.isDeactivationQuery();
    }

    @PostConstruct
    private void init() {

        adminTextCommandsHandler = handlers.stream().collect(Collectors.toMap(
                SomeAdminMessageHandler::availableFor,
                element -> element::handle
        ));
    }
}
