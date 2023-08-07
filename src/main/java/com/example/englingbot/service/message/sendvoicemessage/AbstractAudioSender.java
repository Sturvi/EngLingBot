package com.example.englingbot.service.message.sendvoicemessage;

import com.example.englingbot.service.externalapi.telegram.EnglishWordLearningBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;

/**
 * Abstract class for sending audio messages through Telegram.
 */
// TODO Абстрактный класс нельзя инстанцировать, компонент тут не имеет смысла
@Component
@Scope("prototype")
@Slf4j
public abstract class AbstractAudioSender {

    private final EnglishWordLearningBot englishWordLearningBot;
    private SendAudio sendAudio;

    /**
     * Constructor for initializing the audio sender with the given Telegram bot application.
     *
     * @param englishWordLearningBot the Telegram bot application
     */
    protected AbstractAudioSender(EnglishWordLearningBot englishWordLearningBot) {
        this.englishWordLearningBot = englishWordLearningBot;
        this.sendAudio = new SendAudio();
        log.debug("AbstractAudioSender initialized");
    }

    /**
     * Creates a new audio object for sending.
     *
     * @return this instance of AbstractAudioSender
     */
    protected AbstractAudioSender newAudio() {
        this.sendAudio = new SendAudio();
        log.debug("New audio created");
        return this;
    }

    /**
     * Sets the chat ID for the audio message.
     *
     * @param chatId the chat ID
     * @return this instance of AbstractAudioSender
     */
    protected AbstractAudioSender setChatId(Long chatId) {
        sendAudio.setChatId(chatId);
        log.debug("Chat ID set: {}", chatId);
        return this;
    }

    /**
     * Sets the audio file to be sent.
     *
     * @param file the audio file
     * @return this instance of AbstractAudioSender
     */
    protected AbstractAudioSender setAudio(File file) {
        InputFile inputFile = new InputFile(file);
        sendAudio.setAudio(inputFile);
        log.debug("Audio file ID set: {}", inputFile);
        return this;
    }

    /**
     * Sets the title for the audio message.
     *
     * @param title the title of the audio
     * @return this instance of AbstractAudioSender
     */
    protected AbstractAudioSender setTitle(String title) {
        sendAudio.setTitle(title);
        log.debug("Audio title set: {}", title);
        return this;
    }

    /**
     * Sends the audio message with the given settings.
     *
     * @return the sent Message object
     */
    protected Message send() {
        Message message = null;
        try {
            message = englishWordLearningBot.execute(sendAudio);
            log.debug("Audio sent to chat: {}", sendAudio.getChatId());
        } catch (Exception e) {
            log.error("An error occurred while sending the audio: {}", e.getMessage());
        }

        return message;
    }
}