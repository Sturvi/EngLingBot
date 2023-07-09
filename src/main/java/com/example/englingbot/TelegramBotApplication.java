package com.example.englingbot;

import com.example.englingbot.service.handlers.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramBotApplication extends TelegramLongPollingBot {

    private final String botUsername;
    private final UpdateHandler updateHandler;

    public TelegramBotApplication(@Value("${bot.token}") String botToken,
                                  @Value("${bot.username}") String botUsername, UpdateHandler updateHandler) {
        super(botToken);
        this.botUsername = botUsername;
        this.updateHandler = updateHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var botEvent = BotEvent.getTelegramObject(update);

        try {
            updateHandler.handle(botEvent);
        } catch (Exception e) {
            log.error("An error occurred: ", e);
            e.printStackTrace();
        }
    }



    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
