package com.example.englingbot.service.message.sendvoicemessage;

import com.example.englingbot.service.externalapi.telegram.TelegramBotApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;

/**
 * Class for sending audio messages to a specific user through Telegram.
 */
@Component
@Scope("prototype")
@Slf4j
public class SendAudioForUser extends AbstractAudioSender {

    /**
     * Constructor for initializing the SendAudioForUser with the given Telegram bot application.
     *
     * @param telegramBotApplication the Telegram bot application
     */
    public SendAudioForUser(TelegramBotApplication telegramBotApplication) {
        super(telegramBotApplication);
    }

    /**
     * Sends an audio message to the specified chat ID with the given audio file.
     * The title of the audio is set as "Произношение слова".
     *
     * @param chatId     the chat ID to send the audio to
     * @param audioFile  the audio file to send
     * @return the sent Message object
     */
    public Message sendAudio(Long chatId, File audioFile) {
        log.info("Sending audio to chat ID: {}", chatId);

        return newAudio()
                .setChatId(chatId)
                .setAudio(audioFile)
                .setTitle("Произношение слова")
                .send();
    }
}
