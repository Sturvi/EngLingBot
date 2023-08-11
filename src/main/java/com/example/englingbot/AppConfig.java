package com.example.englingbot;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@PropertySource("classpath:secrets.properties")
public class AppConfig {


    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }


    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public ExecutorService chatGPTExecutorService(@Value("${chatGPT.executorService.nThreads}") int nGptThreads) {
        return Executors.newFixedThreadPool(nGptThreads);
    }

    @Bean
    public ExecutorService botExecutorService(@Value("${bot.executorService.nThreads}") int nBotThreads) {
        return Executors.newFixedThreadPool(nBotThreads);
    }
}

