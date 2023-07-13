package com.example.englingbot.service.sendmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Класс MessageSenderFactory предоставляет методы для создания экземпляров класса MessageSender.
 */
@Component
@RequiredArgsConstructor
@Lazy
public class SendMessageForUserFactory {

    private final ApplicationContext context;

    /**
     * Создает новый экземпляр MessageSender.
     *
     * @return Новый экземпляр MessageSender.
     */
    public SendMessageForUser createMessageSender() {
        return context.getBean(SendMessageForUser.class);
    }
}
