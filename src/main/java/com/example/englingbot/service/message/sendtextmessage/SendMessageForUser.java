package com.example.englingbot.service.message.sendtextmessage;

import com.example.englingbot.service.externalapi.telegram.TelegramBotApplication;
import com.example.englingbot.service.keyboards.ReplyKeyboardMarkupFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * This class represents a message sender for users.
 */
@Component
@Scope("prototype")
@Slf4j
public class SendMessageForUser extends AbstractMessageSender {

    public SendMessageForUser(TelegramBotApplication telegramBotApplication) {
        super(telegramBotApplication);
    }

    /**
     * Sends a message to the specified chat ID with the given text and default ReplyKeyboardMarkup.
     *
     * @param chatId       The ID of the chat to send the message to.
     * @param messageText  The text of the message to send.
     */
    public Message sendMessageWithReplyKeyboard(Long chatId, String messageText) {
        var keyboard = ReplyKeyboardMarkupFactory.getReplyKeyboardMarkup();

        log.info("Sending message to chat ID: {}", chatId);
        log.debug("Message text: {}", messageText);
        log.debug("Using default ReplyKeyboardMarkup");

        return newMessage()
                .setChatId(chatId)
                .setText(messageText)
                .setKeyboardMarkup(keyboard)
                .send();
    }

    /**
     * Sends a message to the specified chat ID with the given text and custom InlineKeyboardMarkup.
     *
     * @param chatId       The ID of the chat to send the message to.
     * @param messageText  The text of the message to send.
     * @param keyboard     The custom InlineKeyboardMarkup to send.
     */
    public Message sendMessageWithInlineKeyboard(Long chatId, String messageText, InlineKeyboardMarkup keyboard) {
        log.info("Sending message to chat ID: {}", chatId);
        log.debug("Message text: {}", messageText);
        log.debug("Using custom InlineKeyboardMarkup");

        return newMessage()
                .setChatId(chatId)
                .setText(messageText)
                .setInlineKeyboard(keyboard)
                .send();
    }
}
