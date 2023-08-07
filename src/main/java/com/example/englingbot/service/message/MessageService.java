package com.example.englingbot.service.message;

import com.example.englingbot.model.UserVocabulary;
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

/**
 * Service class responsible for handling various message operations like sending, editing, and deleting messages.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final EditMessageForUserFactory editMessageForUserFactory;
    private final SendAudioForUserFactory sendAudioForUserFactory;
    private final SendMessageForUserFactory sendMessageForUserFactory;
    private final WordSpeaker wordSpeaker;

    /**
     * Sends a message with a reply keyboard to the specified chat ID.
     *
     * @param chatId       the chat ID to send the message to
     * @param messageText  the text of the message
     * @return the sent Message object
     */
    public Message sendMessage(Long chatId, String messageText) {
        // TODO: Лучше выделить все данные отсылаемого сообщения в отдельный класс,
        //  а в сендере получать объект этого класса и отправлять сообщение.
        //  Нарушен принцип единой ответственности
        return sendMessageForUserFactory
                .createNewMessage()
                .sendMessageWithReplyKeyboard(chatId, messageText);
    }

    /**
     * Sends a message with an inline keyboard to the specified chat ID.
     *
     * @param chatId       the chat ID to send the message to
     * @param messageText  the text of the message
     * @param keyboard     the inline keyboard
     * @return the sent Message object
     */
    public Message sendMessageWithInlineKeyboard(Long chatId, String messageText, InlineKeyboardMarkup keyboard) {
        return sendMessageForUserFactory
                .createNewMessage()
                .sendMessageWithInlineKeyboard(chatId, messageText, keyboard);
    }

    /**
     * Edits a message with an inline keyboard.
     *
     * @param botEvent     the event information
     * @param messageText  the new message text
     * @param keyboard     the inline keyboard
     */
    public void editMessageWithInlineKeyboard(BotEvent botEvent, String messageText, InlineKeyboardMarkup keyboard) {
        editMessageForUserFactory
                .createNewMessage()
                .editMessageWithInlineKeyboard(botEvent, messageText, keyboard);
    }

    /**
     * Deletes the inline keyboard from a message.
     *
     * @param botEvent the event information
     */
    public void deleteInlineKeyboard(BotEvent botEvent){
        editMessageForUserFactory
                .createNewMessage()
                .deleteInlineKeyboard(botEvent);
    }

    /**
     * Edits the text and deletes the inline keyboard from a message.
     *
     * @param botEvent    the event information
     * @param messageText the new message text
     */
    public void editTextAndDeleteInlineKeyboard(BotEvent botEvent, String messageText){
        editMessageForUserFactory
                .createNewMessage()
                .editTextAndDeleteInlineKeyboard(botEvent, messageText);
    }

    /**
     * Sends an audio message to the specified chat ID.
     *
     * @param chatId     the chat ID to send the audio to
     * @param audioFile  the audio file to send
     * @return the sent Message object
     */
    public Message sendAudio(Long chatId, File audioFile){
        return sendAudioForUserFactory
                .createNewAudio()
                .sendAudio(chatId, audioFile);
    }

    /**
     * Sends an audio message with a word and the associated inline keyboard.
     *
     * @param chatId      the chat ID to send the audio to
     * @param userWord    the user vocabulary object
     * @param messageText the text of the message
     */
    public void sendAudioWithWord(Long chatId, UserVocabulary userWord, String messageText){
        try {
            File audio = wordSpeaker.getVoice(userWord.getWord());
            sendAudio(chatId, audio);
        } catch (Exception e) {
            log.error(e.toString());
        }

        var keyboard = InlineKeyboardMarkupFactory.getKeyboardForCurrentWordInUserWordList(userWord, userWord.getWord().toString());

        sendMessageWithInlineKeyboard(chatId, messageText, keyboard);
    }
}
