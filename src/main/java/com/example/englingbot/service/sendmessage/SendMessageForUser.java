package com.example.englingbot.service.sendmessage;

import com.example.englingbot.TelegramBotApplication;
import com.example.englingbot.service.keyboards.ReplyKeyboardMarkupFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SendMessageForUser extends MessageSender{
    /**
     * Конструктор MessageSender.
     *
     * @param telegramBotApplication Экземпляр приложения TelegramBot.
     */
    public SendMessageForUser(TelegramBotApplication telegramBotApplication) {
        super(telegramBotApplication);
    }

    public void sendMessage (Long chatId, String messageText){
        var keyboard = ReplyKeyboardMarkupFactory.getReplyKeyboardMarkup();

        newMessage()
                .setChatId(chatId)
                .setText(messageText)
                .setKeyboardMarkup(keyboard)
                .send();
    }
}
