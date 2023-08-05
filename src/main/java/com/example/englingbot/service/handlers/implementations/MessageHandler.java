package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import com.example.englingbot.service.voice.WordSpeaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    /**
     * Handles the incoming bot event and associated app user.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        TextCommandsEnum incomingCommand = TextCommandsEnum.fromString(botEvent.getText());

        var handlerMethod = textCommandsHandler.get(incomingCommand);
        handlerMethod.accept(botEvent, appUser);
    }

    /**
     * Initializes the text commands handler.
     */
    @PostConstruct
    void init() {
        log.debug("Initializing text commands handler");
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
        log.debug("Finished initializing text commands handler");
    }

    private void handleDelete(BotEvent botEvent, AppUser appUser) {
        // Not implemented
    }

    private void handleStatistic(BotEvent botEvent, AppUser appUser) {
        // Not implemented
    }

    private void handleAddRandomWords(BotEvent botEvent, AppUser appUser) {
        // Not implemented
    }

    private void handleListRepetitionWords(BotEvent botEvent, AppUser appUser) {
        // Not implemented
    }

    private void handleListStudyWords(BotEvent botEvent, AppUser appUser) {
        // Not implemented
    }

    /**
     * Handles the mixed mode command.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    private void handleMixedMode(BotEvent botEvent, AppUser appUser) {
        appUser.setUserState(UserStateEnum.MIXED);

        userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING, UserWordState.REPETITION);
    }

    /**
     * Handles the repeat word command.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    private void handleRepeatWord(BotEvent botEvent, AppUser appUser) {
        appUser.setUserState(UserStateEnum.REPETITION);

        userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.REPETITION);
    }

    /**
     * Handles the learn word command.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    private void handleLearnWord(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleLearnWord method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.LEARNING);

        userVocabularyService.sendRandomWord(botEvent.getId(), appUser, UserWordState.LEARNING);

        log.debug("Finished handleLearnWord method for event: {}", botEvent);
    }

    /**
     * Handles the add word command.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    private void handleAddWord(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleAddWord method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ADD_MENU);

        templateMessagesSender.sendAddWordMessage(botEvent.getId());

        log.debug("Finished handleAddWord method for event: {}", botEvent);
    }

    /**
     * Handles the answer command.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    private void handleAnswer(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleAnswer method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ANSWER);

        messageService
                .sendMessage(botEvent.getId(), "Пришлите пожалуйста ваш вопрос. \\n\\n" +
                        "Примечание: получение ответа может занять некоторое время");

        log.debug("Finished handleAnswer method for event: {}", botEvent);
    }

    /**
     * Handles the start and help commands.
     *
     * @param botEvent Event information from the bot.
     * @param appUser  Associated application user.
     */
    private void handleStartAndHelp(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleStartAndHelp method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.MAIN);

        templateMessagesSender.sendStartAndHelpMessage(botEvent.getId());

        log.debug("Finished handleStartAndHelp method for event: {}", botEvent);
    }
}
