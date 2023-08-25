package com.example.englingbot.service.user.handlers.implementations.messagehandlers;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.Handler;
import com.example.englingbot.service.user.handlers.interfaces.SomeMessageHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageHandler implements Handler {
    private final List<SomeMessageHandler> handlers;
    private Map<UserTextCommandsEnum, BiConsumer<BotEvent, AppUser>> textCommandsHandler;

    /**
     * Handles the incoming bot event and associated app user.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        UserTextCommandsEnum incomingCommand = UserTextCommandsEnum.fromString(botEvent.getText());

        var handlerMethod = textCommandsHandler.get(incomingCommand);

        handlerMethod.accept(botEvent, appUser);
    }

    @Override
    public boolean canHandle(BotEvent botEvent, AppUser appUser) {
        return appUser.getRole() == UserRoleEnum.USER
                && botEvent.isMessage()
                && !botEvent.isDeactivationQuery();
    }

    /**
     * Initializes the text commands handler.
     */
    @PostConstruct
    private void init() {

        textCommandsHandler = handlers.stream().collect(Collectors.toMap(
                SomeMessageHandler::availableFor,
                element -> element::handle
        ));
    }
}
