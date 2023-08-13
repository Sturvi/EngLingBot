package com.example.englingbot.service.message;

import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.message.publishers.implementations.DeleteMessageForUser;
import com.example.englingbot.service.message.publishers.implementations.EditMessageForUser;
import com.example.englingbot.service.message.publishers.implementations.SendAudioForUser;
import com.example.englingbot.service.message.publishers.implementations.SendMessageForUser;
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
public class MessageService {
    private final EditMessageForUser editMessageForUser;
    private final SendAudioForUser sendAudioForUser;
    private final SendMessageForUser sendMessageForUser;
    private final DeleteMessageForUser deleteMessageForUser;


    public CompletableFuture<Message> sendMessage(Long chatId, String messageText) {
        log.trace("Sending message: {}", messageText);
        return sendMessageForUser.sendMessage(chatId, messageText);
    }

    public CompletableFuture<Message> sendMessageWithKeyboard(Long chatId, String messageText, ReplyKeyboard keyboard) {
        log.trace("Sending message with inline keyboard: {}", messageText);
        return sendMessageForUser.sendMessageWithInlineKeyboard(chatId, messageText, keyboard);
    }

    public void editMessageWithInlineKeyboard(BotEvent botEvent, String messageText, InlineKeyboardMarkup keyboard) {
        editMessageForUser.editMessageWithInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), messageText, keyboard);
    }

    public void deleteInlineKeyboard(BotEvent botEvent) {
        editMessageForUser.editTextAndDeleteInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), botEvent.getText());
    }

    public void editTextAndDeleteInlineKeyboard(BotEvent botEvent, String messageText) {
        editMessageForUser.editTextAndDeleteInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), messageText);
    }

    public void deleteMessage (Long chatId, Integer messageId) {
        deleteMessageForUser.deleteMessage(chatId, messageId);
    }

    public CompletableFuture<Message> sendAudio(Long chatId, String title, File audioFile) {
        return sendAudioForUser.sendAudio(chatId, title, audioFile);
    }
}
