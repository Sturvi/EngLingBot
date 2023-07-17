package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.WordListTypeEnum;
import com.example.englingbot.service.UserService;
import com.example.englingbot.service.UserWordListService;
import com.example.englingbot.service.enums.TextCommandsEnum;
import com.example.englingbot.service.handlers.Handler;
import com.example.englingbot.service.sendmessage.SendMessageForUserFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
@Slf4j
class MessageHandler implements Handler {

    private final SendMessageForUserFactory sendMessageForUserFactory;
    private final UserService userService;
    private final UserWordListService userWordListService;

    private final Map<TextCommandsEnum, Consumer<BotEvent>> textCommandsHandler;

    /**
     * Component that handles incoming messages from the bot user.
     * Implements the {@link Handler} interface.
     */
    MessageHandler(SendMessageForUserFactory sendMessageForUserFactory, UserService userService, UserWordListService userWordListService) {
        this.sendMessageForUserFactory = sendMessageForUserFactory;
        this.userService = userService;
        this.userWordListService = userWordListService;
        textCommandsHandler = new HashMap<>();
    }

    /**
     * Handles the BotEvent by retrieving the command from the event's text and
     * invoking the corresponding command handler.
     *
     * @param botEvent the event to handle.
     */
    @Override
    public void handle(BotEvent botEvent) {
        TextCommandsEnum incomingCommand = TextCommandsEnum.fromString(botEvent.getText());

        var handlerMethod = textCommandsHandler.get(incomingCommand);
        handlerMethod.accept(botEvent);
    }

    /**
     * Initializes the MessageHandler by mapping the command handlers to the appropriate commands.
     */
    @PostConstruct
    private void init() {
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
        textCommandsHandler.put(null, this::handleDefault);
    }

    /**
     * Default handler for unrecognized commands.
     *
     * @param botEvent the event to handle.
     */
    private void handleDefault(BotEvent botEvent) {
    }

    /**
     * Handler for delete command.
     *
     * @param botEvent the event to handle.
     */
    private void handleDelete(BotEvent botEvent) {

    }

    /**
     * Handler for statistic command.
     *
     * @param botEvent the event to handle.
     */
    private void handleStatistic(BotEvent botEvent) {

    }

    /**
     * Handler for add random words command.
     *
     * @param botEvent the event to handle.
     */
    private void handleAddRandomWords(BotEvent botEvent) {

    }

    /**
     * Handler for list repetition words command.
     *
     * @param botEvent the event to handle.
     */
    private void handleListRepetitionWords(BotEvent botEvent) {

    }

    /**
     * Handler for list study words command.
     *
     * @param botEvent the event to handle.
     */
    private void handleListStudyWords(BotEvent botEvent) {

    }

    /**
     * Handler for mixed mode command.
     *
     * @param botEvent the event to handle.
     */
    private void handleMixedMode(BotEvent botEvent) {

    }

    /**
     * Handler for repeat word command.
     *
     * @param botEvent the event to handle.
     */
    private void handleRepeatWord(BotEvent botEvent) {
    }


    /**
     * Handler for learn word command. This handler first checks if there are any words to learn for the user.
     * If there are, it sends a message with the word to learn. If there aren't, it sends a message suggesting
     * to add more words or to use the word bank.
     *
     * @param botEvent the event to handle.
     */
    private void handleLearnWord(BotEvent botEvent) {
        log.debug("Starting handleLearnWord method for event: {}", botEvent);
        var messageSender = sendMessageForUserFactory.createMessageSender();
        var user = userService.getAppUser(botEvent);
        var userWord = userWordListService.getRandomUserWordList(user, WordListTypeEnum.LEARNING);

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

    /**
     * Handler for add word command. This handler changes the user's state to ADD_MENU and sends a message
     * to the user with instructions on how to add words.
     *
     * @param botEvent the event to handle.
     */
    private void handleAddWord(BotEvent botEvent) {
        log.debug("Starting handleAddWord method for event: {}", botEvent);
        userService.changeAppUserState(UserStateEnum.ADD_MENU, botEvent);
        sendMessageForUserFactory
                .createMessageSender()
                .sendMessage(botEvent.getId(), """
                        Можете отправлять слова, которые хотите добавить в свою коллекцию.\s

                        Если нужно добавить несколько слов, можете отправлять их по очереди.

                        Можете отправлять также словосочетания

                        Учтите, что слова переводятся автоматически, с помощью сервисов онлайн перевода и никак не проходят дополнительные проверки орфографии. Поэтому даже при небольших ошибках, перевод также будет ошибочный.""");

        log.debug("Finished handleAddWord method for event: {}", botEvent);
    }

    /**
     * Handler for answer command. This handler changes the user's state to ANSWER and sends a message
     * to the user asking for their question.
     *
     * @param botEvent the event to handle.
     */
    private void handleAnswer(BotEvent botEvent) {
        log.debug("Starting handleAnswer method for event: {}", botEvent);
        userService.changeAppUserState(UserStateEnum.ANSWER, botEvent);
        sendMessageForUserFactory
                .createMessageSender()
                .sendMessage(botEvent.getId(), "Пришлите пожалуйста ваш вопрос. \n\nПримечание: получение ответа может занять некоторое время");

        log.debug("Finished handleAnswer method for event: {}", botEvent);
    }

    /**
     * Handler for start and help command. This handler changes the user's state to NORMAL and sends a message
     * to the user with the help text.
     *
     * @param botEvent the event to handle.
     */
    private void handleStartAndHelp(BotEvent botEvent) {
        log.debug("Starting handleStartAndHelp method for event: {}", botEvent);
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
