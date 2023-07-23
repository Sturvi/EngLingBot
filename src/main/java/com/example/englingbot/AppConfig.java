package com.example.englingbot;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

}

