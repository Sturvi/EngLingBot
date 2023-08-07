package com.example.englingbot.service.message.edittextmessage;

import com.example.englingbot.service.externalapi.telegram.EnglishWordLearningBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Abstract class for editing Telegram messages.
 */
@Slf4j
// TODO Как и в остальных случаях, компонент не имеет смысла над абстрактным классом
@Component
@Scope("prototype")
public abstract class AbstractMessageEditor {

    private final EnglishWordLearningBot englishWordLearningBot;
    private EditMessageText editMessageText;

    /**
     * Constructor for initializing the message editor with the given Telegram bot application.
     *
     * @param englishWordLearningBot the Telegram bot application
     */
    protected AbstractMessageEditor(EnglishWordLearningBot englishWordLearningBot) {
        this.englishWordLearningBot = englishWordLearningBot;
        this.editMessageText = new EditMessageText();
        log.debug("AbstractMessageEditor initialized");
    }

    /**
     * Resets the edit message object for editing.
     *
     * @return this instance of AbstractMessageEditor
     */
    protected AbstractMessageEditor editMessage() {
        this.editMessageText = new EditMessageText();
        log.debug("Message ready for editing");
        return this;
    }

    /**
     * Sets the chat ID for the message to be edited.
     *
     * @param chatId the chat ID
     * @return this instance of AbstractMessageEditor
     */
    protected AbstractMessageEditor setChatId(Long chatId) {
        editMessageText.setChatId(chatId.toString());
        log.debug("Chat ID set: {}", chatId);
        return this;
    }

    /**
     * Sets the message ID for the message to be edited.
     *
     * @param messageId the message ID
     * @return this instance of AbstractMessageEditor
     */
    protected AbstractMessageEditor setMessageId(Integer messageId) {
        editMessageText.setMessageId(messageId);
        log.debug("Message ID set: {}", messageId);
        return this;
    }

    /**
     * Sets the text for the message to be edited.
     *
     * @param text the message text
     * @return this instance of AbstractMessageEditor
     */
    protected AbstractMessageEditor setText(String text) {
        editMessageText.setText(text);
        log.debug("Message text set: {}", text);
        return this;
    }

    /**
     * Sets the inline keyboard for the message to be edited.
     *
     * @param keyboard the inline keyboard
     * @return this instance of AbstractMessageEditor
     */
    protected AbstractMessageEditor setInlineKeyboard(InlineKeyboardMarkup keyboard) {
        editMessageText.setReplyMarkup(keyboard);
        log.debug("Inline keyboard for the message set");
        return this;
    }

    /**
     * Edits the message with the given settings. Enables Markdown and HTML.
     */
    protected void edit() {
        editMessageText.enableMarkdown(true);
        editMessageText.enableHtml(true);

        try {
            englishWordLearningBot.execute(editMessageText);
            log.debug("Message edited in chat: {}", editMessageText.getChatId());
        } catch (Exception e) {
            log.error("An error occurred while editing the message: {}", e.getMessage());
        }
    }
}
