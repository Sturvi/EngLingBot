package com.example.englingbot.service.externalapi.telegram;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.AppUserService;
import com.example.englingbot.service.handlers.implementations.UpdateHandler;
import com.example.englingbot.service.message.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutorService;

/**
 * Main class for handling incoming updates and messages from Telegram.
 */
@Component
@Slf4j
public class EnglishWordLearningBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final UpdateHandler updateHandler;
    private final AppUserService appUserService;
    private final ExecutorService botExecutorService;

    /**
     * Constructor for TelegramBotApplication.
     *
     * @param botToken           The token for the bot.
     * @param botUsername        The username for the bot.
     * @param updateHandler      Handler for updates.
     * @param appUserService     Service for handling AppUser.
     * @param botExecutorService The executor service for handling updates.
     */
    public EnglishWordLearningBot(@Value("${bot.token}") String botToken,
                                  @Value("${bot.username}") String botUsername,
                                  UpdateHandler updateHandler,
                                  AppUserService appUserService,
                                  @Qualifier("botExecutorService") ExecutorService botExecutorService) {
        super(botToken);
        this.botUsername = botUsername;
        this.updateHandler = updateHandler;
        this.appUserService = appUserService;
        this.botExecutorService = botExecutorService;
    }


    /**
     * Handles incoming updates from Telegram.
     *
     * @param update The update received from Telegram.
     */
    @Override
    public void onUpdateReceived(Update update) {
        botExecutorService.execute(() -> {
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
        });
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


    @EventListener
    public void handleMessageEvent(MessageEvent<?> event) {
        log.trace("Handling message event: {}", event);

        try {
            switch (event.getMessageType()) {
                case SEND_MESSAGE -> {
                    log.trace("Processing SEND_MESSAGE");
                    event.setResponse(execute(event.getSendMessage()));
                }
                case EDIT_MESSAGE_TEXT -> {
                    log.trace("Processing EDIT_MESSAGE_TEXT");
                    execute(event.getEditMessageText());
                }
                case SEND_AUDIO -> {
                    log.trace("Processing SEND_AUDIO");
                    event.setResponse(execute(event.getSendAudio()));
                }
                case DELETE_MESSAGE -> {
                    log.trace("Processing DELETE_MESSAGE");
                    execute(event.getDeleteMessage());
                }
            }
        } catch (TelegramApiException e) {
            log.error("Error handling message event", e);
        }
    }

}
