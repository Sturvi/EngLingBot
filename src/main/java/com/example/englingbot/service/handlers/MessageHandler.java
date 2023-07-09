package com.example.englingbot.service.handlers;

import com.example.englingbot.BotEvent;
import com.example.englingbot.service.sendmessage.MessageSenderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MessageHandler implements Handler{

    private final MessageSenderFactory messageSenderFactory;

    @Override
    public void handle(BotEvent botEvent) {
        messageSenderFactory
                .createMessageSender()
                .newMessage()
                .setChatId(botEvent.getId())
                .setText("Ответ")
                .send();

    }
}
