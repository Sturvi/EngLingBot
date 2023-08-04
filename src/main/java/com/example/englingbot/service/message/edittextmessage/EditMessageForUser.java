package com.example.englingbot.service.message.edittextmessage;

import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.externalapi.telegram.TelegramBotApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;

@Component
@Scope("prototype")
@Slf4j
public class EditMessageForUser extends AbstractMessageEditor {

    public EditMessageForUser(TelegramBotApplication telegramBotApplication) {
        super(telegramBotApplication);
    }

    public void editMessageWithInlineKeyboard(BotEvent botEvent, String messageText, InlineKeyboardMarkup keyboard) {
        log.info("Editing message in chat ID: {}", botEvent.getId());
        log.debug("Message text: {}", messageText);
        log.debug("Using custom InlineKeyboardMarkup");

        editMessage()
                .setChatId(botEvent.getId())
                .setMessageId(botEvent.getMessageId())
                .setText(messageText)
                .setInlineKeyboard(keyboard)
                .edit();
    }

    public void deleteInlineKeyboard(BotEvent botEvent){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(new ArrayList<>());
        editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboardMarkup);
    }

    public void editTextAndDeleteInlineKeyboard(BotEvent botEvent, String messageText){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(new ArrayList<>());
        editMessageWithInlineKeyboard(botEvent, messageText, keyboardMarkup);
    }
}
