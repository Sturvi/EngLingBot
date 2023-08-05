package com.example.englingbot.service.message;

import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.edittextmessage.EditMessageForUserFactory;
import com.example.englingbot.service.message.sendtextmessage.SendMessageForUserFactory;
import com.example.englingbot.service.message.sendvoicemessage.SendAudioForUserFactory;
import com.example.englingbot.service.voice.WordSpeaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final EditMessageForUserFactory editMessageForUserFactory;
    private final SendAudioForUserFactory sendAudioForUserFactory;
    private final SendMessageForUserFactory sendMessageForUserFactory;
    private final WordSpeaker wordSpeaker;


    public Message sendMessage(Long chatId, String messageText) {
        return sendMessageForUserFactory
                .createNewMessage()
                .sendMessageWithReplyKeyboard(chatId, messageText);
    }

    public Message sendMessageWithInlineKeyboard(Long chatId, String messageText, InlineKeyboardMarkup keyboard) {
        return sendMessageForUserFactory
                .createNewMessage()
                .sendMessageWithInlineKeyboard(chatId, messageText, keyboard);
    }

    public void editMessageWithInlineKeyboard(BotEvent botEvent, String messageText, InlineKeyboardMarkup keyboard) {
        editMessageForUserFactory
                .createNewMessage()
                .editMessageWithInlineKeyboard(botEvent, messageText, keyboard);
    }

    public void deleteInlineKeyboard(BotEvent botEvent){
        editMessageForUserFactory
                .createNewMessage()
                .deleteInlineKeyboard(botEvent);
    }

    public void editTextAndDeleteInlineKeyboard(BotEvent botEvent, String messageText){
        editMessageForUserFactory
                .createNewMessage()
                .editTextAndDeleteInlineKeyboard(botEvent, messageText);
    }

    public Message sendAudio(Long chatId, File audioFile){
        return sendAudioForUserFactory
                .createNewAudio()
                .sendAudio(chatId, audioFile);
    }

    public void sendAudioWithWord (Long chatId, UserVocabulary userWord, String messageText){
        try {
            File audio = wordSpeaker.getVoice(userWord.getWord());
            sendAudio(chatId, audio);
        } catch (Exception e) {
            log.error(e.toString());
        }

        var keyboard = InlineKeyboardMarkupFactory.getKeyboardForCurrentWordInUserWordList(userWord);

        sendMessageWithInlineKeyboard(chatId, messageText, keyboard);
    }


}
