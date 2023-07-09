package com.example.englingbot.service.handlers;

import com.example.englingbot.BotEvent;
import com.example.englingbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Класс UpdateHandler обрабатывает события бота и обновления от пользователей.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateHandler implements Handler {

    private final UserService userService;
    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    /**
     * Метод обработки событий бота. Сохраняет или обновляет информацию о пользователе, затем обрабатывает деактивацию,
     * сообщения или callback-запросы от пользователя.
     *
     * @param botEvent событие бота для обработки.
     */
    @Override
    public void handle(BotEvent botEvent) {
        userService.saveOrUpdateUser(botEvent.getFrom());
        log.debug("Обновление или сохранение информации о пользователе: {}", botEvent.getFrom());

        try {
            if (botEvent.isDeactivationQuery()) {
                log.debug("Обработка запроса на деактивацию от пользователя: {}", botEvent.getFrom());
                userService.deactivateUser(botEvent);
            } else if (botEvent.isMessage()) {
                log.debug("Обработка сообщения от пользователя: {}", botEvent.getFrom());
                messageHandler.handle(botEvent);
            } else if (botEvent.isCallbackQuery()) {
                log.debug("Обработка callback-запроса с данными: {}, chat ID: {}", botEvent.getData(), botEvent.getId());
                callbackQueryHandler.handle(botEvent);
            }
        } catch (Exception e) {
            log.error("Произошла ошибка при обработке объекта Telegram", e);
        }
    }
}
