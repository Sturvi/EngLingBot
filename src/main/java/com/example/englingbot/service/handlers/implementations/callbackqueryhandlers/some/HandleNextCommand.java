package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleNextCommand implements SomeCallbackQueryHandler {
    private final UserVocabularyService userVocabularyService;
    private final MessageService messageService;
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Next' command for bot event: {}", botEvent);
        var userState = appUser.getUserState();

        switch (userState) {
            case LEARNING -> sendWordForUser(botEvent.getId(), appUser, UserWordState.LEARNING);
            case REPETITION -> sendWordForUser(botEvent.getId(), appUser, UserWordState.REPETITION);
            case MIXED -> sendWordForUser(botEvent.getId(), appUser, UserWordState.LEARNING, UserWordState.REPETITION);
            default -> messageService.sendMessageToUser(botEvent.getId(), "Для изучения слов выберите пожалуйста соответствующее меню");
        }
    }

    public void sendWordForUser(Long chatId, AppUser appUser, UserWordState... userWordStates) {
        var userVocabularyOpt = userVocabularyService.getRandomUserVocabulary(appUser, userWordStates);
        var messageText = userVocabularyService.getMessageText(userVocabularyOpt);
        log.debug("Retrieved messageText for appUser: {}. Message present: {}", appUser.getId(), messageText.isPresent());

        if (messageText.isPresent() && userVocabularyOpt.isPresent()) {
            log.debug("Sending audio message for chatId: {} and appUser: {}", chatId, appUser.getId());
            templateMessagesSender.sendAudioWithWord(chatId, userVocabularyOpt.get(), messageText.get());
        } else {
            log.debug("No word to send for chatId: {} and appUser: {}. Sending no word message.", chatId, appUser.getId());
            templateMessagesSender.sendNoWordToSendMessage(chatId, userWordStates);
        }
    }




    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NEXT;
    }
}
