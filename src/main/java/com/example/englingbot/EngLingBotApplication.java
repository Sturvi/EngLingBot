package com.example.englingbot;

import com.example.englingbot.service.externalapi.telegram.EnglishWordLearningBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * The main class of the Spring Boot application.
 */
@SpringBootApplication
@Slf4j
public class EngLingBotApplication {

    // TODO Вот это лучше в отдельный файл с конфигурацией, чтобы остался один main метод
    private final EnglishWordLearningBot bot;


    /**
     * Creates an instance of the EngLingBotApplication class.
     *
     * @param bot  an instance of the TelegramBotApplication class.
     */
    public EngLingBotApplication(EnglishWordLearningBot bot) {
        this.bot = bot;
        log.debug("An instance of the EngLingBotApplication class has been created.");
    }

    /**
     * The main method that launches the Spring Boot application.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        log.info("The application is starting.");
        SpringApplication.run(EngLingBotApplication.class, args);
    }

    /**
     * Creates and returns an instance of the TelegramBotsApi.
     * Registers the bot with the API.
     *
     * @return an instance of TelegramBotsApi.
     * @throws IllegalStateException if the bot cannot be registered.
     */
    @Bean
    public TelegramBotsApi telegramBotsApi() {
        // TODO  не критично, но я бы перенёс конфигурацию в отдельный файлик
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
