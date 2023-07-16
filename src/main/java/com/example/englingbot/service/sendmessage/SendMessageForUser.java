package com.example.englingbot.service.sendmessage;

import com.example.englingbot.TelegramBotApplication;
import com.example.englingbot.service.keyboards.ReplyKeyboardMarkupFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class represents a message sender for users.
 */
@Component
@Scope("prototype")
@Slf4j
public class SendMessageForUser extends MessageSender {

    public SendMessageForUser(TelegramBotApplication telegramBotApplication) {
        super(telegramBotApplication);
    }

    /**
     * Sends a message to the specified chat ID with the given text.
     *
     * @param chatId       The ID of the chat to send the message to.
     * @param messageText  The text of the message to send.
     */
    public void sendMessage(Long chatId, String messageText) {
        var keyboard = ReplyKeyboardMarkupFactory.getReplyKeyboardMarkup();

        log.info("Sending message to chat ID: {}", chatId);
        log.debug("Message text: {}", messageText);

        newMessage()
                .setChatId(chatId)
                .setText(messageText)
                .setKeyboardMarkup(keyboard)
                .send();
    }
}
