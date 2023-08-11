package com.example.englingbot.service.message.publishers.implementations;

import com.example.englingbot.service.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@Component
@Slf4j
public class DeleteMessageForUser extends AbstractMessageEventPublisher {

    public DeleteMessageForUser(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();

        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);

        send(deleteMessage);
    }
}
