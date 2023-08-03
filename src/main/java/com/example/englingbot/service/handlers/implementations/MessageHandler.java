package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.WordListTypeEnum;
import com.example.englingbot.service.AppUserService;
import com.example.englingbot.service.UserWordListService;
import com.example.englingbot.service.enums.TextCommandsEnum;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.sendmessage.SendMessageForUserFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
@Slf4j
class MessageHandler implements Handler {

    private final SendMessageForUserFactory sendMessageForUserFactory;
    private final AppUserService appUserService;
    private final UserWordListService userWordListService;
    private final DefaultMessageHandler defaultMessageHandler;

    private final Map<TextCommandsEnum, BiConsumer<BotEvent, AppUser>> textCommandsHandler;

    MessageHandler(SendMessageForUserFactory sendMessageForUserFactory, AppUserService appUserService, UserWordListService userWordListService, DefaultMessageHandler defaultMessageHandler) {
        this.sendMessageForUserFactory = sendMessageForUserFactory;
        this.appUserService = appUserService;
        this.userWordListService = userWordListService;
        this.defaultMessageHandler = defaultMessageHandler;
        textCommandsHandler = new HashMap<>();
    }

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        TextCommandsEnum incomingCommand = TextCommandsEnum.fromString(botEvent.getText());

        var handlerMethod = textCommandsHandler.get(incomingCommand);
        handlerMethod.accept(botEvent, appUser);
    }

    @PostConstruct
    void init() {
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
    }

    private void handleRepeatWord(BotEvent botEvent, AppUser appUser) {
    }

    private void handleLearnWord(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleLearnWord method for event: {}", botEvent);
        var messageSender = sendMessageForUserFactory.createMessageSender();
        var userWord = userWordListService.getRandomUserWordList(appUser, WordListTypeEnum.LEARNING);

        if (userWord == null) {
            log.debug("User has no words to learn, sending a message to add more words");
            messageSender.sendMessage(botEvent.getId(), "У вас нет слов для изучения в данный момент. Пожалуйста, " +
                    "добавьте новые слова, или воспользуйтесь нашим банком слов.");
        } else {
            log.debug("User has words to learn, sending the word to user");
            //НАПИСАТЬ МЕТОДЫ ДЛЯ РАБОТЫ С ПРОИЗНОШЕНИЯМИ
            String messageText = userWordListService.getUserWordListString(userWord);
            messageSender.sendMessage(botEvent.getId(), messageText);
        }
        log.debug("Finished handleLearnWord method for event: {}", botEvent);
    }

    private void handleAddWord(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleAddWord method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ADD_MENU);
        sendMessageForUserFactory
                .createMessageSender()
                .sendMessage(botEvent.getId(), """
                        Можете отправлять слова, которые хотите добавить в свою коллекцию.\\s

                        Если нужно добавить несколько слов, можете отправлять их по очереди.

                        Можете отправлять также словосочетания

                        Учтите, что слова переводятся автоматически, с помощью сервисов онлайн перевода и никак не проходят дополнительные проверки орфографии. Поэтому даже при небольших ошибках, перевод также будет ошибочный.""");

        log.debug("Finished handleAddWord method for event: {}", botEvent);
    }

    private void handleAnswer(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleAnswer method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.ANSWER);
        sendMessageForUserFactory
                .createMessageSender()
                .sendMessage(botEvent.getId(), "Пришлите пожалуйста ваш вопрос. \\n\\nПримечание: получение ответа может занять некоторое время");

        log.debug("Finished handleAnswer method for event: {}", botEvent);
    }

    private void handleStartAndHelp(BotEvent botEvent, AppUser appUser) {
        log.debug("Starting handleStartAndHelp method for event: {}", botEvent);
        appUser.setUserState(UserStateEnum.MAIN);
        String startAndHelpMessage = """
                Привет! Я - Word Learning Bot, и я помогу тебе учить английские слова. Вот список доступных команд и функций, которые ты можешь использовать:

                Команды, доступные из меню:

                /start - начать общение с ботом и получить приветственное сообщение.
                /answer - отправить вопрос о языке и получить ответ.
                /help - получить список доступных команд и их описание.
                /statistic - просмотреть статистику изучения слов.
                /delete - удалить слово из вашего словаря.

                Кнопки внизу телеграм бота:

                📒 Добавить слова - добавить слова или словосочетания в ваш словарь.
                👨🏻‍🎓 Учить слова - начать изучать новые слова из вашего словаря.
                🔄 Повторять слова - повторять изученные слова из вашего словаря.
                🔀 Смешанный режим - режим изучения и повторения слов в случайном порядке.
                📓 Список изучаемых слов - просмотреть список слов, которые вы изучаете.
                📓 Список слов на повторении - просмотреть список слов, которые вы повторяете.
                📖 Добавить случайные слова - добавить случайные слова в ваш словарь.
                Приятного обучения!

                Если у вас возникли вопросы, жалобы или предложения, свяжитесь с администратором: @SturviBots
                """;
        var messageSender = sendMessageForUserFactory.createMessageSender();
        messageSender.sendMessage(botEvent.getId(), startAndHelpMessage);

        log.debug("Finished handleStartAndHelp method for event: {}", botEvent);
    }
}
