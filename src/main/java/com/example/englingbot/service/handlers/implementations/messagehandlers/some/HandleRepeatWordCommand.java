package com.example.englingbot.service.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleRepeatWordCommand implements SomeMessageHandler {
    private final UserVocabularyService userVocabularyService;
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Handling botEvent for appUser: {}", appUser.getId());

        appUser.setUserState(UserStateEnum.REPETITION);
        log.debug("Set appUser state to REPETITION for user: {}", appUser.getId());

        var userVocabularyOpt = userVocabularyService.getRandomUserVocabulary(appUser, UserWordState.REPETITION);
        var messageText = userVocabularyService.getMessageText(userVocabularyOpt);
        log.debug("Retrieved messageText for appUser: {}. Message present: {}", appUser.getId(), messageText.isPresent());

        if (messageText.isPresent() && userVocabularyOpt.isPresent()){
            log.debug("Sending audio message for botEvent: {} and appUser: {}", botEvent.getId(), appUser.getId());
            templateMessagesSender.sendAudioWithWord(botEvent.getId(), userVocabularyOpt.get(), messageText.get());
        } else {
            log.debug("No word to send for botEvent: {} and appUser: {}. Sending no word message.", botEvent.getId(), appUser.getId());
            templateMessagesSender.sendNoWordToSendMessage(botEvent.getId(), UserWordState.REPETITION);
        }
    }


    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.REPEAT_WORD;
    }
}
