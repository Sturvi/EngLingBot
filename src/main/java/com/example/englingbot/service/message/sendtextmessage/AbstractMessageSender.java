package com.example.englingbot.service.message.sendtextmessage;

import com.example.englingbot.service.externalapi.telegram.EnglishWordLearningBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * The MessageSender class provides functionality to send messages to the user.
 */
@Slf4j
@Scope("prototype")
public abstract class AbstractMessageSender {

    private final EnglishWordLearningBot englishWordLearningBot;
    private SendMessage sendMessage;

    /**
     * MessageSender constructor.
     *
     * @param englishWordLearningBot Instance of the TelegramBot application.
     */
    protected AbstractMessageSender(EnglishWordLearningBot englishWordLearningBot) {
        this.englishWordLearningBot = englishWordLearningBot;
        this.sendMessage = new SendMessage();
    }

    /**
     * Creates a new message.
     *
     * @return This MessageSender object.
     */
    protected AbstractMessageSender newMessage() {
        this.sendMessage = new SendMessage();
        log.debug("New message created");
        return this;
    }

    /**
     * Sets the chat ID for the message.
     *
     * @param chatId Chat ID.
     * @return This MessageSender object.
     */
    protected AbstractMessageSender setChatId(Long chatId) {
        sendMessage.setChatId(chatId);
        log.debug("Chat ID set: {}", chatId);
        return this;
    }

    /**
     * Sets the text of the message.
     *
     * @param text Text of the message.
     * @return This MessageSender object.
     */
    protected AbstractMessageSender setText(String text) {
        sendMessage.setText(text);
        log.debug("Message text set: {}", text);
        return this;
    }

    /**
     * Sets an inline keyboard for the message.
     *
     * @param keyboard Inline keyboard.
     * @return This MessageSender object.
     */
    protected AbstractMessageSender setInlineKeyboard(InlineKeyboardMarkup keyboard) {
        sendMessage.setReplyMarkup(keyboard);
        log.debug("Inline keyboard for the message set");
        return this;
    }

    protected AbstractMessageSender setKeyboardMarkup (ReplyKeyboardMarkup keyboard) {
        sendMessage.setReplyMarkup(keyboard);
        log.debug("KeyboardMarkup keyboard for the message set");
        return this;
    }

    /**
     * Sends the message to the user.
     */
    protected Message send() {
        sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        Message message = null;

        try {
            message = englishWordLearningBot.execute(sendMessage);
            log.debug("Message sent to chat: {}", sendMessage.getChatId());
        } catch (Exception e) {
            log.error("An error occurred while sending the message: {}", e.getMessage());
        }

        return message;
    }
}
