package com.example.englingbot.service.telegrambot.message.publishers.implementations;

import com.example.englingbot.service.telegrambot.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.File;

@Slf4j
@Component
public class SendDocumentMessage extends AbstractMessageEventPublisher {

    public SendDocumentMessage(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    public void sendDocument(Long chatId, String caption, ReplyKeyboardMarkup keyboard, File document) {
        SendDocument sendDocument = new SendDocument();

        sendDocument.setChatId(chatId.toString());
        sendDocument.setCaption(caption);
        sendDocument.setReplyMarkup(keyboard);

        InputFile inputFile = new InputFile(document);

        sendDocument.setDocument(inputFile);

        send(sendDocument);
    }
}
