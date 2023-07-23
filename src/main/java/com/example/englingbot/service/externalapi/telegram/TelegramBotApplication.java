package com.example.englingbot.service.externalapi.telegram;

import com.example.englingbot.service.externalapi.chatgpt.ChatGpt;
import com.example.englingbot.service.externalapi.googleapi.GoogleTranslator;
import com.example.englingbot.service.handlers.implementations.UpdateHandler;
import com.example.englingbot.service.sendmessage.SendMessageForUser;
import com.example.englingbot.service.sendmessage.SendMessageForUserFactory;
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
    private final SendMessageForUserFactory sendMessageForUserFactory;
    private final ChatGpt chatGpt;


    public TelegramBotApplication(@Value("${bot.token}") String botToken,
                                  @Value("${bot.username}") String botUsername, UpdateHandler updateHandler, GoogleTranslator googleTranslator, SendMessageForUserFactory sendMessageForUserFactory, ChatGpt chatGpt) {
        super(botToken);
        this.botUsername = botUsername;
        this.updateHandler = updateHandler;
        this.sendMessageForUserFactory = sendMessageForUserFactory;
        this.chatGpt = chatGpt;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var botEvent = BotEvent.getTelegramObject(update);

        String resp = chatGpt.chat(botEvent.getText());
        sendMessageForUserFactory.createMessageSender().sendMessage(botEvent.getId(), resp);

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
