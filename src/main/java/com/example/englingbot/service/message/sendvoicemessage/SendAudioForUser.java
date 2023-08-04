package com.example.englingbot.service.message.sendvoicemessage;

import com.example.englingbot.service.externalapi.telegram.TelegramBotApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;

@Component
@Scope("prototype")
@Slf4j
public class SendAudioForUser extends AbstractAudioSender {

    public SendAudioForUser(TelegramBotApplication telegramBotApplication) {
        super(telegramBotApplication);
    }

    public Message sendAudio(Long chatId, File audioFile) {
        log.info("Sending audio to chat ID: {}", chatId);

        return newAudio()
                .setChatId(chatId)
                .setAudio(audioFile)
                .setTitle("Произношение слова")
                .send();
    }
}
