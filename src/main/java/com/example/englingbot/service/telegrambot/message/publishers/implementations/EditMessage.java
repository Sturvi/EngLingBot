package com.example.englingbot.service.telegrambot.message.publishers.implementations;

import com.example.englingbot.service.telegrambot.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;


@Component
@Slf4j
public class EditMessage extends AbstractMessageEventPublisher {


    public EditMessage(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    public void editMessageWithInlineKeyboard(Long chatId, Integer messageId, String messageText, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessage = new EditMessageText();

        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(messageText);
        editMessage.setReplyMarkup(keyboard);

        send(editMessage);
    }


    public void editTextAndDeleteInlineKeyboard(Long chatId, Integer messageId, String messageText) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(new ArrayList<>());

        editMessageWithInlineKeyboard(chatId, messageId, messageText, keyboardMarkup);
    }
}
