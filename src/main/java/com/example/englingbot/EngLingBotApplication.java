package com.example.englingbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Главный класс приложения Spring Boot.
 */
@SpringBootApplication
public class EngLingBotApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngLingBotApplication.class);

    private final TelegramBotApplication bot;

    /**
     * Создает экземпляр класса EngLingBotApplication.
     *
     * @param bot экземпляр класса TelegramBotApplication.
     */
    public EngLingBotApplication(TelegramBotApplication bot) {
        this.bot = bot;
        LOGGER.debug("Создан экземпляр класса EngLingBotApplication.");
    }

    /**
     * Главный метод, который запускает приложение Spring Boot.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        LOGGER.info("Запуск приложения.");
        SpringApplication.run(EngLingBotApplication.class, args);
    }

    /**
     * Создает и возвращает экземпляр TelegramBotsApi.
     * Регистрирует бота в API.
     *
     * @return экземпляр TelegramBotsApi.
     * @throws IllegalStateException если бот не может быть зарегистрирован.
     */
    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            LOGGER.info("Создание и регистрация бота в API.");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            return botsApi;
        } catch (Exception e) {
            LOGGER.error("Ошибка при регистрации бота.", e);
            throw new IllegalStateException(e);
        }
    }
}
