package com.example.englingbot.service.message.editmessage;

import com.example.englingbot.service.externalapi.telegram.TelegramBotApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Slf4j
@Component
@Scope("prototype")
public abstract class AbstractMessageEditor {

    private final TelegramBotApplication telegramBotApplication;
    private EditMessageText editMessageText;

    protected AbstractMessageEditor(TelegramBotApplication telegramBotApplication) {
        this.telegramBotApplication = telegramBotApplication;
        this.editMessageText = new EditMessageText();
    }

    protected AbstractMessageEditor editMessage() {
        this.editMessageText = new EditMessageText();
        log.debug("Message ready for editing");
        return this;
    }

    protected AbstractMessageEditor setChatId(Long chatId) {
        editMessageText.setChatId(chatId.toString());
        log.debug("Chat ID set: {}", chatId);
        return this;
    }

    protected AbstractMessageEditor setMessageId(Integer messageId) {
        editMessageText.setMessageId(messageId);
        log.debug("Message ID set: {}", messageId);
        return this;
    }

    protected AbstractMessageEditor setText(String text) {
        editMessageText.setText(text);
        log.debug("Message text set: {}", text);
        return this;
    }

    protected AbstractMessageEditor setInlineKeyboard(InlineKeyboardMarkup keyboard) {
        editMessageText.setReplyMarkup(keyboard);
        log.debug("Inline keyboard for the message set");
        return this;
    }

    protected void edit() {
        editMessageText.enableMarkdown(true);
        editMessageText.enableHtml(true);

        try {
            telegramBotApplication.execute(editMessageText);
            log.debug("Message edited in chat: {}", editMessageText.getChatId());
        } catch (Exception e) {
            log.error("An error occurred while editing the message: {}", e.getMessage());
        }
    }
}
