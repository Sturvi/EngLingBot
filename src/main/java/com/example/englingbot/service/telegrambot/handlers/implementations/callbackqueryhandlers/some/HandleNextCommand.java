package com.example.englingbot.service.telegrambot.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.telegrambot.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import com.example.englingbot.service.telegrambot.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
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

        if (Objects.requireNonNull(userState) == UserStateEnum.LEARNING) {
            handleLeaningMode(appUser);
        } else {
            telegramMessageService.sendMessageToUser(botEvent.getId(), "Для изучения слов выберите пожалуйста соответствующее меню");
        }
    }

    private void handleLeaningMode(AppUser appUser) {
        int randomNumber = random.nextInt(10);
        Optional<UserVocabulary> userVocabularyOpt = randomNumber == 0
                ? userVocabularyService.getRandomUserVocabulary(appUser, UserWordState.LEARNED)
                : Optional.empty();

        if (userVocabularyOpt.isEmpty()) {
            userVocabularyOpt = userVocabularyService.getRandomUserVocabulary(appUser, UserWordState.LEARNING);
        }

        sendMessage(appUser, userVocabularyOpt, UserWordState.LEARNING);
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
