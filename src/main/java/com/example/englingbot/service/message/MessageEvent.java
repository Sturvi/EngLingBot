package com.example.englingbot.service.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;

@Getter
public class MessageEvent<T> extends ApplicationEvent {
    public enum MessageType {
        SEND_MESSAGE,
        EDIT_MESSAGE_TEXT,
        SEND_AUDIO,
        DELETE_MESSAGE
    }

    private final MessageType messageType;
    private SendMessage sendMessage;
    private EditMessageText editMessageText;
    private SendAudio sendAudio;
    private DeleteMessage deleteMessage;
    @Setter
    private CompletableFuture<Message> futureMessage;

    public MessageEvent(Object source, SendMessage message) {
        super(source);
        this.messageType = MessageType.SEND_MESSAGE;
        this.sendMessage = message;
    }

    public MessageEvent(Object source, EditMessageText message) {
        super(source);
        this.messageType = MessageType.EDIT_MESSAGE_TEXT;
        this.editMessageText = message;
    }

    public MessageEvent(Object source, SendAudio message) {
        super(source);
        this.messageType = MessageType.SEND_AUDIO;
        this.sendAudio = message;
    }

    public MessageEvent(Object source, DeleteMessage message) {
        super(source);
        this.messageType = MessageType.DELETE_MESSAGE;
        this.deleteMessage = message;
    }

    public void setResponse(Message response) {
        if (futureMessage != null) {
            futureMessage.complete(response);
        }
    }
}
