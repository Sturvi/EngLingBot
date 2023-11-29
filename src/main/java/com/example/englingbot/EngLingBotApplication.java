package com.example.englingbot;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
@Theme(value = "my-app", variant = Lumo.DARK)
public class EngLingBotApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        log.info("The application is starting.");
        SpringApplication.run(EngLingBotApplication.class, args);
    }

}
