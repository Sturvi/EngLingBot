package com.example.englingbot.service.handlers;

import com.example.englingbot.service.externalapi.telegram.BotEvent;

/**
 * The interface for a handler that processes bot events.
 */
public interface Handler {

    /**
     * Handles a given bot event.
     *
     * @param botEvent The bot event to be handled.
     */
    void handle(BotEvent botEvent);
}
