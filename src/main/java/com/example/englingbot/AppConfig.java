package com.example.englingbot;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    // TODO Не замечание, просто комментарий, можно ничего не менять.
    //  В корпоративном мире разработки обычно используется jackson,
    //  который поставляется стартером spring-boot-starter-web
    @Bean
    public Gson gson() {
        return new Gson();
    }

    // TODO параметр nThreads лучше перенести в проперти и устанавливать
    //  его значение из переменных окружения, это позволит подтюнить производительность без перекомпиляции
    @Bean
    public ExecutorService chatGPTExecutorService() {
        return Executors.newFixedThreadPool(2);
    }

}

