package com.example.englingbot.service.message.publishers.implementations;

import com.example.englingbot.service.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
public class SendAudioForUser extends AbstractMessageEventPublisher {

    public SendAudioForUser(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    public CompletableFuture<Message> sendAudio(Long chatId, String title, File audioFile) {
        log.trace("Entering sendAudio method");
        SendAudio sendAudio = new SendAudio();

        sendAudio.setChatId(chatId);

        sendAudio.setTitle(title);

        InputFile inputFile = new InputFile(audioFile);
        sendAudio.setAudio(inputFile);
        log.debug("Set chatId: {}, title: {}, audio file: {}", chatId, title, audioFile.getName());

        return send(sendAudio);
    }
}
