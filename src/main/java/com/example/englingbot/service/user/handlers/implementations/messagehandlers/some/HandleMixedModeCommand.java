package com.example.englingbot.service.user.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleMixedModeCommand implements SomeMessageHandler {
    private final UserVocabularyService userVocabularyService;
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        appUser.setUserState(UserStateEnum.MIXED);

        var userVocabularyOpt = userVocabularyService.getRandomUserVocabulary(appUser, UserWordState.LEARNING, UserWordState.REPETITION);
        var messageText = userVocabularyService.getMessageText(userVocabularyOpt);

        if (messageText.isPresent() && userVocabularyOpt.isPresent()){
            templateMessagesSender.sendAudioWithWord(botEvent.getId(), userVocabularyOpt.get(), messageText.get());
        } else {
            templateMessagesSender.sendNoWordToSendMessage(botEvent.getId(), UserWordState.LEARNING, UserWordState.REPETITION);
        }
    }

    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.MIXED_MODE;
    }
}
