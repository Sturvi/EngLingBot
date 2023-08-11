package com.example.englingbot.service.handlers.implementations.messagehandlers;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.implementations.defaultmessagehandlers.DefaultMessageHandler;
import com.example.englingbot.service.handlers.interfaces.Handler;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
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
    private final DefaultMessageHandler defaultMessageHandler;
    private Map<TextCommandsEnum, BiConsumer<BotEvent, AppUser>> textCommandsHandler;

    /**
     * Handles the incoming bot event and associated app user.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        TextCommandsEnum incomingCommand = TextCommandsEnum.fromString(botEvent.getText());

        var handlerMethod = textCommandsHandler.get(incomingCommand);

        if (handlerMethod == null) {
            defaultMessageHandler.handle(botEvent, appUser);
            return;
        }

        handlerMethod.accept(botEvent, appUser);
    }

    /**
     * Initializes the text commands handler.
     */
    @PostConstruct
    private void init() {
        log.debug("Initializing CallbackQueryHandler");
        textCommandsHandler = handlers.stream().collect(Collectors.toMap(
                SomeMessageHandler::availableFor,
                element -> element::handle
        ));
    }
}
