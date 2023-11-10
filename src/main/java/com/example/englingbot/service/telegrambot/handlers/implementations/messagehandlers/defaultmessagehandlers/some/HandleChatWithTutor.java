package com.example.englingbot.service.telegrambot.handlers.implementations.messagehandlers.defaultmessagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.externalapi.openai.TutorService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.keyboards.ReplyKeyboardMarkupFactory;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeDefaultMessageHandler;
import com.example.englingbot.service.voice.VoiceSpeaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleChatWithTutor implements SomeDefaultMessageHandler {
    private final TutorService tutorService;
    private final TelegramMessageService telegramMessageService;
    private final VoiceSpeaker voiceSpeaker;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var keyboard = ReplyKeyboardMarkupFactory.getTutorChatKeyboard();

        String response = tutorService.sendMessage(appUser, botEvent.getText());
        String reviewResponse = tutorService.reviewUserMessage(botEvent.getText());

        var responseVoice = voiceSpeaker.getVoiceFileFromString(response);
        responseVoice.ifPresent(voice -> telegramMessageService.sendAudio(botEvent.getId(), "Response", voice));

        String messageText = response + "\n\n<b>Text Review and Correction:</b>\n".toUpperCase() + reviewResponse;

        telegramMessageService.sendMessageWithKeyboard(botEvent.getId(), messageText, keyboard);
    }

    @Override
    public UserStateEnum availableFor() {
        return UserStateEnum.CHAT_WITH_TUTOR;
    }
}
