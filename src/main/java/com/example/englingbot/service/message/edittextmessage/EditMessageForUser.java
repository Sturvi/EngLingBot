package com.example.englingbot.service.message.edittextmessage;

import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.externalapi.telegram.EnglishWordLearningBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;

/**
 * Class for editing messages for a user, including handling inline keyboards.
 */
@Component
@Scope("prototype")
@Slf4j
public class EditMessageForUser extends AbstractMessageEditor {

    /**
     * Constructor for initializing the EditMessageForUser with the given Telegram bot application.
     *
     * @param englishWordLearningBot the Telegram bot application
     */
    public EditMessageForUser(EnglishWordLearningBot englishWordLearningBot) {
        super(englishWordLearningBot);
    }

    /**
     * Edits a message with the given text and inline keyboard.
     *
     * @param botEvent    the event information
     * @param messageText the new message text
     * @param keyboard    the inline keyboard
     */
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

    /**
     * Deletes the inline keyboard from a message.
     *
     * @param botEvent the event information
     */
    public void deleteInlineKeyboard(BotEvent botEvent) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(new ArrayList<>());
        editMessageWithInlineKeyboard(botEvent, botEvent.getText(), keyboardMarkup);
        log.debug("Inline keyboard deleted for message ID: {}", botEvent.getMessageId());
    }

    /**
     * Edits the text and deletes the inline keyboard from a message.
     *
     * @param botEvent    the event information
     * @param messageText the new message text
     */
    public void editTextAndDeleteInlineKeyboard(BotEvent botEvent, String messageText) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(new ArrayList<>());
        editMessageWithInlineKeyboard(botEvent, messageText, keyboardMarkup);
        log.debug("Message text edited and inline keyboard deleted for message ID: {}", botEvent.getMessageId());
    }
}
