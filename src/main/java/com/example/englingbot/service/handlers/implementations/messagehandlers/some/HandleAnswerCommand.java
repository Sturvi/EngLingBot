package com.example.englingbot.service.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleAnswerCommand implements SomeMessageHandler {
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handle Answer Command for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ANSWER);

        messageService
                .sendMessage(botEvent.getId(), "Пришлите пожалуйста ваш вопрос. \\n\\n" +
                        "Примечание: получение ответа может занять некоторое время");

        log.debug("Finished handle Answer Command for event: {}", botEvent);
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.ANSWER;
    }
}
