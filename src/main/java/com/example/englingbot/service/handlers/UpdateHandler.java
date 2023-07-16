package com.example.englingbot.service.handlers;

import com.example.englingbot.BotEvent;
import com.example.englingbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The UpdateHandler class handles bot events and user updates.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateHandler implements Handler {

    private final UserService userService;
    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    /**
     * The method of handling bot events. It saves or updates user information, then processes the deactivation,
     * messages, or callback queries from the user.
     *
     * @param botEvent The bot event to process.
     */
    @Override
    public void handle(BotEvent botEvent) {
        userService.saveOrUpdateUser(botEvent.getFrom());
        log.debug("Update or save user information: {}", botEvent.getFrom());

        try {
            if (botEvent.isDeactivationQuery()) {
                log.debug("Processing the deactivation request from the user: {}", botEvent.getFrom());
                userService.deactivateUser(botEvent);
            } else if (botEvent.isMessage()) {
                log.debug("Processing the message from the user: {}", botEvent.getFrom());
                messageHandler.handle(botEvent);
            } else if (botEvent.isCallbackQuery()) {
                log.debug("Processing callback request with data: {}, chat ID: {}", botEvent.getData(), botEvent.getId());
                callbackQueryHandler.handle(botEvent);
            }
        } catch (Exception e) {
            log.error("An error occurred while processing the Telegram object", e);
        }
    }
}
