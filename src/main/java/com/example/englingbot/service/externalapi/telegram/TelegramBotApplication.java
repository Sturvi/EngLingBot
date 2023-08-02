package com.example.englingbot.service.externalapi.telegram;

import com.example.englingbot.model.Word;
import com.example.englingbot.service.WordService;
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
