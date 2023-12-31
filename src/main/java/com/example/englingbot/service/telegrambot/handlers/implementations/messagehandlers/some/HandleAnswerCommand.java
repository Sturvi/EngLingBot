package com.example.englingbot.service.telegrambot.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.telegrambot.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleAnswerCommand implements SomeMessageHandler {
    private final TelegramMessageService telegramMessageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Starting handle Answer Command for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ANSWER);

        telegramMessageService
                .sendMessageToUser(botEvent.getId(), "Пришлите пожалуйста ваш вопрос. \n\n" +
                        "Примечание: получение ответа может занять некоторое время");

        log.trace("Finished handle Answer Command for event: {}", botEvent);
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.ANSWER;
    }
}
