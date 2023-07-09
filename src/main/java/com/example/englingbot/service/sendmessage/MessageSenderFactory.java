package com.example.englingbot.service.sendmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Класс MessageSenderFactory предоставляет методы для создания экземпляров класса MessageSender.
 */
@Component
@RequiredArgsConstructor
public class MessageSenderFactory {

    private final ApplicationContext context;

    /**
     * Создает новый экземпляр MessageSender.
     *
     * @return Новый экземпляр MessageSender.
     */
    public MessageSender createMessageSender() {
        return context.getBean(MessageSender.class);
    }
}
