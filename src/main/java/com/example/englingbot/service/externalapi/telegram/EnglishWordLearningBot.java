package com.example.englingbot.service.externalapi.telegram;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.AppUserService;
import com.example.englingbot.service.handlers.implementations.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Main class for handling incoming updates and messages from Telegram.
 */
@Component
@Slf4j
public class EnglishWordLearningBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final UpdateHandler updateHandler;
    private final AppUserService appUserService;

    /**
     * Constructor for TelegramBotApplication.
     *
     * @param botToken      The token for the bot.
     * @param botUsername   The username for the bot.
     * @param updateHandler Handler for updates.
     * @param appUserService Service for handling AppUser.
     */
    public EnglishWordLearningBot(@Value("${bot.token}") String botToken,
                                  @Value("${bot.username}") String botUsername,
                                  UpdateHandler updateHandler,
                                  AppUserService appUserService) {
        super(botToken);
        this.botUsername = botUsername;
        this.updateHandler = updateHandler;
        this.appUserService = appUserService;
    }

    /**
     * Handles incoming updates from Telegram.
     *
     * @param update The update received from Telegram.
     */
    @Override
    public void onUpdateReceived(Update update) {
        var botEvent = BotEvent.getTelegramObject(update);
        AppUser appUser = appUserService.saveOrUpdateAppUser(botEvent.getFrom());
        log.debug("Update or save user information: {}", botEvent.getFrom());

        try {
            updateHandler.handle(botEvent, appUser);
        } catch (Exception e) {
            log.error("An error occurred: ", e);
            e.printStackTrace();
        }

        appUserService.save(appUser);
    }

    /**
     * Returns the bot's username.
     *
     * @return The bot's username.
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
