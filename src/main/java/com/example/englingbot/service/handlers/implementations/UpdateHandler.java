package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.AppUserService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
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

    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    /**
     * The method of handling bot events. It saves or updates user information, then processes the deactivation,
     * messages, or callback queries from the user.
     *
     * @param botEvent The bot event to process.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        try {
            if (botEvent.isDeactivationQuery()) {
                log.debug("Processing the deactivation request from the user: {}", botEvent.getFrom());
                appUser.setUserStatus(false);
            } else if (botEvent.isMessage()) {
                log.debug("Processing the message from the user: {}", botEvent.getFrom());
                messageHandler.handle(botEvent, appUser);
            } else if (botEvent.isCallbackQuery()) {
                log.debug("Processing callback request with data: {}, chat ID: {}", botEvent.getData(), botEvent.getId());
                callbackQueryHandler.handle(botEvent, appUser);
            }
        } catch (Exception e) {
            log.error("An error occurred while processing the Telegram object", e);
        }
    }
}
