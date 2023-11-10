package com.example.englingbot.service.telegrambot.message.publishers;

import com.example.englingbot.service.telegrambot.message.MessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    protected CompletableFuture<Message> send(SendMessage sendMessage) {
        log.trace("Sending message: {}", sendMessage);
        sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        return sendEvent(new MessageEvent(this, sendMessage));
    }

    protected CompletableFuture<Message> send(EditMessageText editMessageText) {
        log.trace("Editing message text: {}", editMessageText);
        editMessageText.enableMarkdown(true);
        editMessageText.enableHtml(true);
        return sendEvent(new MessageEvent(this, editMessageText));
    }

    protected CompletableFuture<Message> send(SendAudio sendAudio) {
        log.trace("Sending audio: {}", sendAudio);
        return sendEvent(new MessageEvent(this, sendAudio));
    }

    protected CompletableFuture<Message> send(DeleteMessage deleteMessage) {
        log.trace("Deleting message with ID: {}", deleteMessage.getMessageId());
        return sendEvent(new MessageEvent(this, deleteMessage));
    }

    protected CompletableFuture<Message> send(SendDocument sendDocument) {
        log.trace("Sending document: {}", sendDocument);
        return sendEvent(new MessageEvent(this, sendDocument));
    }

    private <T> CompletableFuture<T> sendEvent(MessageEvent event) {
        CompletableFuture<T> futureMessage = new CompletableFuture<>();
        // Set the future in the event, so it can be completed later
        event.setFutureMessage(futureMessage);
        log.trace("Publishing message event: {}", event);
        eventPublisher.publishEvent(event);
        return futureMessage;
    }

}
