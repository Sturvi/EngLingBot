package com.example.englingbot;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EngLingBotApplication {


    private final TelegramBotApplication bot;

    /**
     * Создает экземпляр класса EngLingBotApplication.
     *
     * @param bot экземпляр класса TelegramBotApplication.
     */
    public EngLingBotApplication(TelegramBotApplication bot) {
        this.bot = bot;
        log.debug("Создан экземпляр класса EngLingBotApplication.");
    }

    /**
     * Главный метод, который запускает приложение Spring Boot.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        log.info("Запуск приложения.");
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
            log.info("Создание и регистрация бота в API.");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            return botsApi;
        } catch (Exception e) {
            log.error("Ошибка при регистрации бота.", e);
            throw new IllegalStateException(e);
        }
    }
}
