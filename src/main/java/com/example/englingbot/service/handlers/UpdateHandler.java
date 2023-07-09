package com.example.englingbot.service.handlers;

import com.example.englingbot.BotEvent;
import com.example.englingbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateHandler implements Handler{

    private final UserService userService;
    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;


    /**
     * Handles the given TelegramObject by updating user information in the UserRepository and
     * delegating the handling of the update to the appropriate handler based on the type of update.
     *
     * @param botEvent the TelegramObject to handle
     */
    @Override
    public void handle(BotEvent botEvent) {
        userService.saveOrUpdateUser(botEvent.getFrom());

        try {
            if (botEvent.isMessage()) {
                messageHandler.handle(botEvent);
            } else if (botEvent.isCallbackQuery()) {
                log.debug("Handling update for callback with data: {}, chat ID: {}", botEvent.getData(), botEvent.getId());
                callbackQueryHandler.handle(botEvent);
            }
        } catch (Exception e) {
            log.error("An error occurred while handling the Telegram object", e);
        }
    }
}
