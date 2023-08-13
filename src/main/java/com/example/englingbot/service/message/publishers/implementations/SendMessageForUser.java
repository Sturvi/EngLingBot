package com.example.englingbot.service.message.publishers.implementations;

import com.example.englingbot.service.keyboards.ReplyKeyboardMarkupFactory;
import com.example.englingbot.service.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.concurrent.CompletableFuture;

/**
 * This class represents a message sender for users.
 */
@Component
@Slf4j
public class SendMessageForUser extends AbstractMessageEventPublisher {

    public SendMessageForUser(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    private SendMessage createMessage(Long chatId, String messageText, ReplyKeyboard keyboard) {
        log.trace("Creating a new message");

        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }

        log.debug("Message created: {}", message);
        return message;
    }

    public CompletableFuture<Message> sendMessage(Long chatId, String messageText) {
        log.trace("Sending message: {}", messageText);
        return send(createMessage(chatId, messageText, ReplyKeyboardMarkupFactory.getReplyKeyboardMarkup()));
    }


    public CompletableFuture<Message> sendMessageWithInlineKeyboard(Long chatId, String messageText, ReplyKeyboard keyboard) {
        log.trace("Sending message with inline keyboard: {}", messageText);
        return send(createMessage(chatId, messageText, keyboard));
    }
}
