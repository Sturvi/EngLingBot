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
public class HandleLearnWordCommand implements SomeMessageHandler {
    private final UserVocabularyService userVocabularyService;
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handle LearnWord command for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.LEARNING);

        var userVocabularyOpt = userVocabularyService.getRandomUserVocabulary(appUser, UserWordState.LEARNING);
        var messageText = userVocabularyService.getMessageText(userVocabularyOpt);

        if (messageText.isPresent() && userVocabularyOpt.isPresent()){
            templateMessagesSender.sendAudioWithWord(botEvent.getId(), userVocabularyOpt.get(), messageText.get());
        } else {
            templateMessagesSender.sendNoWordToSendMessage(botEvent.getId(), UserWordState.LEARNING);
        }

        log.debug("Finished handle LearnWord command for event: {}", botEvent);
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.LEARN_WORD;
    }
}
