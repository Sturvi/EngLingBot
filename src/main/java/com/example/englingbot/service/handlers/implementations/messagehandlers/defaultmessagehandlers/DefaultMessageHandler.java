package com.example.englingbot.service.handlers.implementations.messagehandlers.defaultmessagehandlers;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.Handler;
import com.example.englingbot.service.handlers.interfaces.SomeDefaultMessageHandler;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.MessageService;
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
public class DefaultMessageHandler implements SomeMessageHandler {
    private final List<SomeDefaultMessageHandler> userStateHandlersList;
    private Map<UserStateEnum, BiConsumer<BotEvent, AppUser>> userStateHandlers;

    /**
     * Handles the given BotEvent and AppUser based on the user's state.
     *
     * @param botEvent The bot event to be handled.
     * @param appUser  The associated app user.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling bot event: {}", botEvent);

        var handlerMethod = userStateHandlers.get(appUser.getUserState());
        if (handlerMethod != null) {
            handlerMethod.accept(botEvent, appUser);
        } else {
            // Handle case when there's no specific handler for the user's state.
            // This is similar to how the 'defaultMessageHandler' was used before.
        }
    }

    @Override
    public TextCommandsEnum availableFor() {
        return null;
    }

    @PostConstruct
    private void init() {
        log.debug("Initializing DefaultMessageHandler");
        userStateHandlers = userStateHandlersList.stream().collect(Collectors.toMap(
                SomeDefaultMessageHandler::availableFor,
                element -> element::handle
        ));
    }
}
