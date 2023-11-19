package com.example.englingbot.service.telegrambot.message;

import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.keyboards.ReplyKeyboardMarkupFactory;
import com.example.englingbot.service.telegrambot.message.publishers.implementations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramMessageService {
    private final EditMessage editMessage;
    private final SendAudio sendAudio;
    private final SendMessage sendMessage;
    private final DeleteMessage deleteMessage;
    private final SendDocumentMessage sendDocument;


    public CompletableFuture<Message> sendMessageToUser(Long chatId, String messageText) {
        log.trace("Sending message: {}", messageText);
        return sendMessage.sendMessageToUserWithKeyboard(chatId, messageText);
    }

    public CompletableFuture<Message> sendMessageToAdmin(Long chatId, String messageText) {
        log.trace("Sending message: {}", messageText);
        return sendMessage.sendMessageToAdminWithKeyboard(chatId, messageText);
    }

    public CompletableFuture<Message> sendMessageWithKeyboard(Long chatId, String messageText, ReplyKeyboard keyboard) {
        log.trace("Sending message with inline keyboard: {}", messageText);
        return sendMessage.sendMessageWithInlineKeyboard(chatId, messageText, keyboard);
    }

    public void sendDocumentToUser(Long chatId, String caption, File documentFile) {
        log.trace("Sending document: {}", documentFile.getName());
        sendDocument.sendDocument(chatId, caption, ReplyKeyboardMarkupFactory.getUserReplyKeyboardMarkup(), documentFile);
    }

    public void editMessageWithInlineKeyboard(BotEvent botEvent, String messageText, InlineKeyboardMarkup keyboard) {
        editMessage.editMessageWithInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), messageText, keyboard);
    }

    public void deleteInlineKeyboard(BotEvent botEvent) {
        editMessage.editTextAndDeleteInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), botEvent.getText());
    }

    public void editTextAndDeleteInlineKeyboard(BotEvent botEvent, String messageText) {
        editMessage.editTextAndDeleteInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), messageText);
    }

    public void deleteMessage (Long chatId, Integer messageId) {
        deleteMessage.deleteMessage(chatId, messageId);
    }

    public CompletableFuture<Message> sendAudio(Long chatId, String title, File audioFile) {
        return sendAudio.sendAudio(chatId, title, audioFile);
    }
}
