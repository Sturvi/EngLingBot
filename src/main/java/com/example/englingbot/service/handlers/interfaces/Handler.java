package com.example.englingbot.service.handlers.interfaces;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.externalapi.telegram.BotEvent;

/**
 * The interface for a handler that processes bot events.
 */
public interface Handler {

    /**
     * Handles a given bot event.
     *
     * @param botEvent The bot event to be handled.
     * @param appUser The user associated with the bot event.
     */
    void handle(BotEvent botEvent, AppUser appUser);

    boolean canHandle(BotEvent botEvent, AppUser appUser);
}
