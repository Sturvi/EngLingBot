package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import com.example.englingbot.service.voice.WordSpeaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
@Slf4j
@RequiredArgsConstructor
class MessageHandler implements Handler {
    private final UserVocabularyService userVocabularyService;
    private final DefaultMessageHandler defaultMessageHandler;
    private final MessageService messageService;
    private final WordSpeaker wordSpeaker;
    private final TemplateMessagesSender templateMessagesSender;
    private Map<TextCommandsEnum, BiConsumer<BotEvent, AppUser>> textCommandsHandler;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        TextCommandsEnum incomingCommand = TextCommandsEnum.fromString(botEvent.getText());

        var handlerMethod = textCommandsHandler.get(incomingCommand);
        handlerMethod.accept(botEvent, appUser);
    }

    @PostConstruct
    void init() {
        textCommandsHandler = new HashMap<>();
        textCommandsHandler.put(TextCommandsEnum.START, this::handleStartAndHelp);
        textCommandsHandler.put(TextCommandsEnum.HELP, this::handleStartAndHelp);
        textCommandsHandler.put(TextCommandsEnum.ANSWER, this::handleAnswer);
        textCommandsHandler.put(TextCommandsEnum.ADD_WORD, this::handleAddWord);
        textCommandsHandler.put(TextCommandsEnum.LEARN_WORD, this::handleLearnWord);
        textCommandsHandler.put(TextCommandsEnum.REPEAT_WORD, this::handleRepeatWord);
        textCommandsHandler.put(TextCommandsEnum.MIXED_MODE, this::handleMixedMode);
        textCommandsHandler.put(TextCommandsEnum.LIST_STUDY_WORDS, this::handleListStudyWords);
        textCommandsHandler.put(TextCommandsEnum.LIST_REPETITION_WORDS, this::handleListRepetitionWords);
        textCommandsHandler.put(TextCommandsEnum.ADD_RANDOM_WORDS, this::handleAddRandomWords);
        textCommandsHandler.put(TextCommandsEnum.STATISTIC, this::handleStatistic);
        textCommandsHandler.put(TextCommandsEnum.DELETE, this::handleDelete);
        textCommandsHandler.put(null, defaultMessageHandler::handle);
    }

    private void handleDelete(BotEvent botEvent, AppUser appUser) {
    }

    private void handleStatistic(BotEvent botEvent, AppUser appUser) {
    }

    private void handleAddRandomWords(BotEvent botEvent, AppUser appUser) {
    }

    private void handleListRepetitionWords(BotEvent botEvent, AppUser appUser) {
    }

    private void handleListStudyWords(BotEvent botEvent, AppUser appUser) {
    }

    private void handleMixedMode(BotEvent botEvent, AppUser appUser) {
        sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING, UserWordState.REPETITION);
    }

    private void handleRepeatWord(BotEvent botEvent, AppUser appUser) {
        sendRandomWord(botEvent.getId(), appUser, UserWordState.REPETITION);
    }

    private void handleLearnWord(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleLearnWord method for event: {}", botEvent);

        sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING);

        log.debug("Finished handleLearnWord method for event: {}", botEvent);
    }

    private void handleAddWord(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleAddWord method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ADD_MENU);

        templateMessagesSender.sendAddWordMessage(botEvent.getId());

        log.debug("Finished handleAddWord method for event: {}", botEvent);
    }

    private void handleAnswer(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleAnswer method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ANSWER);

        messageService
                .sendMessage(botEvent.getId(), "Пришлите пожалуйста ваш вопрос. \\n\\n" +
                        "Примечание: получение ответа может занять некоторое время");

        log.debug("Finished handleAnswer method for event: {}", botEvent);
    }

    private void handleStartAndHelp(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleStartAndHelp method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.MAIN);

        templateMessagesSender.sendStartAndHelpMessage(botEvent.getId());

        log.debug("Finished handleStartAndHelp method for event: {}", botEvent);
    }

    private void sendRandomWord(Long chatId, AppUser appUser, UserWordState... types) {
        var userWord = userVocabularyService.getRandomUserVocabulary(appUser, types);

        if (userWord == null) {
            templateMessagesSender.sendNoWordToSendMessage(chatId, types);
        }

        messageService.sendAudioWithWord(chatId, userWord);
    }

}
