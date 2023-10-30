package com.example.englingbot.configurations;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@PropertySource("classpath:secrets.properties")
public class AppConfiguration {


    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(5 * 1024 * 1024))  // 5MB
                        .build())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofMinutes(1))))
                .build();
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

