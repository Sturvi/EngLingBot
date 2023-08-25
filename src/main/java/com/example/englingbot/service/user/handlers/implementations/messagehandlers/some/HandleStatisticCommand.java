package com.example.englingbot.service.user.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleStatisticCommand implements SomeMessageHandler {
    private final MessageService messageService;
    private final UserVocabularyService userVocabularyService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Entering handle method");
        appUser.setUserState(UserStateEnum.STATISTICS);

        String messageText = userVocabularyService.getUserStatistics(appUser);

        messageService.sendMessageToUser(botEvent.getId(), messageText);
        log.trace("Exiting handle method");
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.STATISTIC;
    }
}
