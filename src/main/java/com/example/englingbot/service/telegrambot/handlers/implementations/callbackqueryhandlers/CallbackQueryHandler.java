package com.example.englingbot.service.telegrambot.handlers.implementations.callbackqueryhandlers;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.service.telegrambot.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.Handler;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeCallbackQueryHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Handler class responsible for managing the callback queries.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryHandler implements Handler {
    private Map<KeyboardDataEnum, BiConsumer<BotEvent, AppUser>> callbackQueryHandlers;
    private final List<SomeCallbackQueryHandler> handlers;

    /**
     * Initializes callback query handlers.
     */
    @PostConstruct
    private void init() {
        log.debug("Initializing CallbackQueryHandler");
        callbackQueryHandlers = handlers.stream().collect(Collectors.toMap(
                SomeCallbackQueryHandler::availableFor,
                element -> element::handle
        ));
    }


    /**
     * Handles a given BotEvent and AppUser.
     *
     * @param botEvent The bot event to be handled.
     * @param appUser  The associated app user.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling bot event: {}", botEvent);
        var dataEnum = KeyboardDataEnum.fromData(botEvent.getData());
        var handler = callbackQueryHandlers.get(dataEnum);

        handler.accept(botEvent, appUser);
    }

    @Override
    public boolean canHandle(BotEvent botEvent, AppUser appUser) {
        return appUser.getRole() == UserRoleEnum.USER &&
                botEvent.isCallbackQuery() &&
                !botEvent.isDeactivationQuery();
    }
}
