package com.example.englingbot.service.externalapi.telegram;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.AppUserService;
import com.example.englingbot.service.handlers.implementations.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class TelegramBotApplication extends TelegramLongPollingBot {

    private final String botUsername;
    private final UpdateHandler updateHandler;
    private final AppUserService appUserService;


    public TelegramBotApplication(@Value("${bot.token}") String botToken,
                                  @Value("${bot.username}") String botUsername, UpdateHandler updateHandler, AppUserService appUserService) {
        super(botToken);
        this.botUsername = botUsername;
        this.updateHandler = updateHandler;
        this.appUserService = appUserService;
    }

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


    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
