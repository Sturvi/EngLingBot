package com.example.englingbot.service.telegrambot.message.publishers.implementations;

import com.example.englingbot.service.telegrambot.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteMessage extends AbstractMessageEventPublisher {

    public DeleteMessage(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage deleteMessage = new org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage();

        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);

        send(deleteMessage);
    }
}
