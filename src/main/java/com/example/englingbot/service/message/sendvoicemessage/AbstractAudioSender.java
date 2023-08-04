package com.example.englingbot.service.message.sendvoicemessage;

import com.example.englingbot.service.externalapi.telegram.TelegramBotApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;

@Component
@Scope("prototype")
@Slf4j
public abstract class AbstractAudioSender {

    private final TelegramBotApplication telegramBotApplication;
    private SendAudio sendAudio;

    protected AbstractAudioSender(TelegramBotApplication telegramBotApplication) {
        this.telegramBotApplication = telegramBotApplication;
        this.sendAudio = new SendAudio();
    }

    protected AbstractAudioSender newAudio() {
        this.sendAudio = new SendAudio();
        log.debug("New audio created");
        return this;
    }

    protected AbstractAudioSender setChatId(Long chatId) {
        sendAudio.setChatId(chatId);
        log.debug("Chat ID set: {}", chatId);
        return this;
    }

    protected AbstractAudioSender setAudio(File file) {
        InputFile inputFile = new InputFile(file);
        sendAudio.setAudio(inputFile);
        log.debug("Audio file ID set: {}", inputFile);
        return this;
    }

    protected AbstractAudioSender setTitle(String title){
        sendAudio.setTitle(title);
        return this;
    }

    protected Message send() {
        Message message = null;
        try {
            message = telegramBotApplication.execute(sendAudio);
            log.debug("Audio sent to chat: {}", sendAudio.getChatId());
        } catch (Exception e) {
            log.error("An error occurred while sending the audio: {}", e.getMessage());
        }

        return message;
    }
}

