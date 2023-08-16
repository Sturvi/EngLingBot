package com.example.englingbot.service.admin.handlers.callbackqueryhandlers;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
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
public class AdminCallbackQueryHandler implements Handler {
    private Map<KeyboardDataEnum, BiConsumer<BotEvent, AppUser>> adminCallbackQueryHandlers;
    private final List<SomeAdminCallbackQueryHandler> handlers;

    @PostConstruct
    private void init() {
        log.debug("Initializing CallbackQueryHandler");
        adminCallbackQueryHandlers = handlers.stream().collect(Collectors.toMap(
                SomeAdminCallbackQueryHandler::availableFor,
                element -> element::handle
        ));
    }

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling bot event: {}", botEvent);
        var dataEnum = KeyboardDataEnum.fromData(botEvent.getData());
        var handler = adminCallbackQueryHandlers.get(dataEnum);

        handler.accept(botEvent, appUser);
    }

    @Override
    public boolean canHandle(BotEvent botEvent, AppUser appUser) {
        return !botEvent.isDeactivationQuery() &&
                appUser.getRole() == UserRoleEnum.ADMIN &&
                botEvent.isCallbackQuery();
    }
}
