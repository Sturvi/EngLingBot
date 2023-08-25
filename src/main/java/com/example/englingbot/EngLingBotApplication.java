package com.example.englingbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class EngLingBotApplication {

    public static void main(String[] args) {
        log.info("The application is starting.");
        SpringApplication.run(EngLingBotApplication.class, args);
    }
}
