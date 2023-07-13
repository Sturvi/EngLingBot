package com.example.englingbot.service.sendmessage;

import com.example.englingbot.TelegramBotApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * Класс MessageSender обеспечивает функциональность отправки сообщений пользователю.
 */
@Slf4j
@Component
@Scope("prototype")
public abstract class MessageSender {

    private final TelegramBotApplication telegramBotApplication;
    private SendMessage sendMessage;

    /**
     * Конструктор MessageSender.
     *
     * @param telegramBotApplication Экземпляр приложения TelegramBot.
     */
    protected MessageSender(TelegramBotApplication telegramBotApplication) {
        this.telegramBotApplication = telegramBotApplication;
        this.sendMessage = new SendMessage();
    }

    /**
     * Создает новое сообщение.
     *
     * @return Этот объект MessageSender.
     */
    protected MessageSender newMessage() {
        this.sendMessage = new SendMessage();
        log.debug("Создано новое сообщение");
        return this;
    }

    /**
     * Устанавливает ID чата для сообщения.
     *
     * @param chatId ID чата.
     * @return Этот объект MessageSender.
     */
    protected MessageSender setChatId(Long chatId) {
        sendMessage.setChatId(chatId);
        log.debug("Установлен ID чата: {}", chatId);
        return this;
    }

    /**
     * Устанавливает текст сообщения.
     *
     * @param text Текст сообщения.
     * @return Этот объект MessageSender.
     */
    protected MessageSender setText(String text) {
        sendMessage.setText(text);
        log.debug("Установлен текст сообщения: {}", text);
        return this;
    }

    /**
     * Устанавливает inline-клавиатуру для сообщения.
     *
     * @param keyboard Inline-клавиатура.
     * @return Этот объект MessageSender.
     */
    protected MessageSender setInlineKeyboard(InlineKeyboardMarkup keyboard) {
        sendMessage.setReplyMarkup(keyboard);
        log.debug("Установлена inline-клавиатура для сообщения");
        return this;
    }

    protected MessageSender setKeyboardMarkup (ReplyKeyboardMarkup keyboard) {
        sendMessage.setReplyMarkup(keyboard);
        log.debug("Установлена KeyboardMarkup-клавиатура для сообщения");
        return this;
    }

    /**
     * Отправляет сообщение пользователю.
     */
    protected void send() {
        sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);

        try {
            telegramBotApplication.execute(sendMessage);
            log.debug("Сообщение отправлено в чат: {}", sendMessage.getChatId());
        } catch (Exception e) {
            log.error("Произошла ошибка при отправке сообщения: {}", e.getMessage());
        }
    }
}
