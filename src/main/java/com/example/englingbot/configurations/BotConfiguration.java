package com.example.englingbot.configurations;

import com.example.englingbot.service.externalapi.telegram.EnglishWordLearningBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BotConfiguration {

    private final EnglishWordLearningBot bot;


    /**
     * Creates and returns an instance of the TelegramBotsApi.
     * Registers the bot with the API.
     *
     * @return an instance of TelegramBotsApi.
     * @throws IllegalStateException if the bot cannot be registered.
     */
    @Bean
    public TelegramBotsApi telegramBotsApi() {

        try {
            log.info("Creating and registering the bot with the API.");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            return botsApi;
        } catch (Exception e) {
            log.error("Error encountered while registering the bot.", e);
            throw new IllegalStateException(e);
        }
    }
}
