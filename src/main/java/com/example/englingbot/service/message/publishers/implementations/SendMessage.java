package com.example.englingbot.service.message.publishers.implementations;

import com.example.englingbot.service.keyboards.ReplyKeyboardMarkupFactory;
import com.example.englingbot.service.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.concurrent.CompletableFuture;

/**
 * This class represents a message sender for users.
 */
@Component
@Slf4j
public class SendMessage extends AbstractMessageEventPublisher {

    public SendMessage(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    private org.telegram.telegrambots.meta.api.methods.send.SendMessage createMessage(Long chatId, String messageText, ReplyKeyboard keyboard) {
        log.trace("Creating a new message");

        var message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }

        log.debug("Message created: {}", message);
        return message;
    }

    public CompletableFuture<Message> sendMessageToUserWithKeyboard(Long chatId, String messageText) {
        log.trace("Sending message: {}", messageText);
        return send(createMessage(chatId, messageText, ReplyKeyboardMarkupFactory.getUserReplyKeyboardMarkup()));
    }

    public CompletableFuture<Message> sendMessageToAdminWithKeyboard(Long chatId, String messageText) {
        log.trace("Sending message: {}", messageText);
        return send(createMessage(chatId, messageText, ReplyKeyboardMarkupFactory.getAdminReplyKeyboardMarkup()));
    }


    public CompletableFuture<Message> sendMessageWithInlineKeyboard(Long chatId, String messageText, ReplyKeyboard keyboard) {
        log.trace("Sending message with inline keyboard: {}", messageText);
        return send(createMessage(chatId, messageText, keyboard));
    }
}
