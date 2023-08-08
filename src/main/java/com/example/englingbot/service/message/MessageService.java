package com.example.englingbot.service.message;

import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.publishers.implementations.EditMessageForUser;
import com.example.englingbot.service.message.publishers.implementations.SendMessageForUser;
import com.example.englingbot.service.message.publishers.implementations.SendAudioForUser;
import com.example.englingbot.service.voice.WordSpeaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final EditMessageForUser editMessageForUser;
    private final SendAudioForUser sendAudioForUser;
    private final SendMessageForUser sendMessageForUser;
    private final WordSpeaker wordSpeaker;


    public CompletableFuture<Message> sendMessage(Long chatId, String messageText) {
        log.trace("Sending message: {}", messageText);
        return sendMessageForUser.sendMessage(chatId, messageText);
    }

    public CompletableFuture<Message> sendMessageWithInlineKeyboard(Long chatId, String messageText, InlineKeyboardMarkup keyboard) {
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

    public CompletableFuture<Message> sendAudio(Long chatId, String title, File audioFile) {
        return sendAudioForUser.sendAudio(chatId, title, audioFile);
    }

    public void sendAudioWithWord(Long chatId, UserVocabulary userWord, String messageText) {
        try {
            File audio = wordSpeaker.getVoice(userWord.getWord());
            sendAudio(chatId, "Произношение слова", audio);
        } catch (Exception e) {
            log.error(e.toString());
        }

        var keyboard = InlineKeyboardMarkupFactory.getKeyboardForCurrentWordInUserWordList(userWord, userWord.getWord().toString());

        sendMessageWithInlineKeyboard(chatId, messageText, keyboard);
    }
}
