package com.example.englingbot.service.user.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Chat;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.openai.TutorService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.ReplyKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.user.handlers.interfaces.SomeMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class HandleChatWIthTutorCommand implements SomeMessageHandler {
    private final MessageService messageService;
    private final TutorService tutorService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        appUser.setUserState(UserStateEnum.CHAT_WITH_TUTOR);

        var keyboard = ReplyKeyboardMarkupFactory.getTutorChatKeyboard();
        Chat chat = tutorService.getNewChat(appUser);

        messageService.sendMessageWithKeyboard(
                botEvent.getId(),
                chat.getMessages().get(1).getContent(),
                keyboard);
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.CHAT_WITH_TUTOR;
    }
}
