package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Component
public class DefaultMessageHandler implements Handler {
    private final Map<UserStateEnum, BiConsumer<BotEvent, AppUser>> userStateHandlers;

    public DefaultMessageHandler() {
        userStateHandlers = Map.of(
                UserStateEnum.ADD_MENU, this::handleAddMenu
        );
    }

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        // Здесь можно добавить логику для выбора соответствующего обработчика на основе состояния пользователя
    }

    private void handleAddMenu(BotEvent botEvent, AppUser appUser) {
        // Здесь можно добавить логику для обработки события, когда состояние пользователя равно ADD_MENU
    }
}
