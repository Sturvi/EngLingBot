package com.example.englingbot.service.user.handlers.implementations.messagehandlers.defaultmessagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Chat;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.externalapi.openai.TutorService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.ReplyKeyboardMarkupFactory;
import com.example.englingbot.service.message.TelegramMessageService;
import com.example.englingbot.service.user.handlers.interfaces.SomeDefaultMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleChatWithTutor implements SomeDefaultMessageHandler {
    private final TutorService tutorService;
    private final TelegramMessageService telegramMessageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var keyboard = ReplyKeyboardMarkupFactory.getTutorChatKeyboard();

        String response = tutorService.sendMessage(appUser, botEvent.getText());

        telegramMessageService.sendMessageWithKeyboard(botEvent.getId(), response, keyboard);
    }

    @Override
    public UserStateEnum availableFor() {
        return UserStateEnum.CHAT_WITH_TUTOR;
    }
}
