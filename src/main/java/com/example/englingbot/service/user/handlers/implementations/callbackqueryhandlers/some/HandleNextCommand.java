package com.example.englingbot.service.user.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.message.TelegramMessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleNextCommand implements SomeCallbackQueryHandler {
    private final UserVocabularyService userVocabularyService;
    private final TelegramMessageService telegramMessageService;
    private final TemplateMessagesSender templateMessagesSender;
    private final Random random = new Random();

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Next' command for bot event: {}", botEvent);
        var userState = appUser.getUserState();

        switch (userState) {
            case LEARNING -> sendWordForUser(appUser, UserWordState.LEARNING);
            case REPETITION -> sendWordForUser(appUser, UserWordState.REPETITION);
            case MIXED -> handleMixedMode(appUser);
            default -> telegramMessageService.sendMessageToUser(botEvent.getId(), "Для изучения слов выберите пожалуйста соответствующее меню");
        }
    }

    private void sendWordForUser(AppUser appUser, UserWordState... userWordStates) {
        var userVocabularyOpt = userVocabularyService.getRandomUserVocabulary(appUser, userWordStates);
        sendMessage(appUser, userVocabularyOpt, userWordStates);
    }

    private void handleMixedMode(AppUser appUser) {
        int randomNumber = random.nextInt(10);
        Optional<UserVocabulary> userVocabularyOpt = randomNumber == 0
                ? userVocabularyService.getRandomUserVocabulary(appUser, UserWordState.LEARNED)
                : Optional.empty();

        if (userVocabularyOpt.isEmpty()) {
            userVocabularyOpt = userVocabularyService.getRandomUserVocabulary(appUser, UserWordState.LEARNING, UserWordState.REPETITION);
        }

        sendMessage(appUser, userVocabularyOpt, UserWordState.LEARNING, UserWordState.REPETITION);
    }

    private void sendMessage(AppUser appUser, Optional<UserVocabulary> userVocabularyOpt, UserWordState... userWordStates) {
        var messageTextOpt = userVocabularyService.getMessageText(userVocabularyOpt);
        log.debug("Retrieved messageText for appUser: {}. Message present: {}", appUser.getId(), messageTextOpt.isPresent());

        if (messageTextOpt.isPresent() && userVocabularyOpt.isPresent()) {
            log.debug("Sending audio message for chatId: {} and appUser: {}", appUser.getTelegramChatId(), appUser.getId());
            templateMessagesSender.sendAudioWithWord(appUser.getTelegramChatId(), userVocabularyOpt.get(), messageTextOpt.get());
        } else {
            log.debug("No word to send for chatId: {} and appUser: {}. Sending no word message.", appUser.getTelegramChatId(), appUser.getId());
            templateMessagesSender.sendNoWordToSendMessage(appUser.getTelegramChatId(), userWordStates);
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.NEXT;
    }
}
