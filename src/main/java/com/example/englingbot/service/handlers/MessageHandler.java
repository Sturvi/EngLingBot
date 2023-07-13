package com.example.englingbot.service.handlers;

import com.example.englingbot.BotEvent;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.WordListTypeEnum;
import com.example.englingbot.service.UserService;
import com.example.englingbot.service.UserWordListService;
import com.example.englingbot.service.enums.TextCommandsEnum;
import com.example.englingbot.service.sendmessage.SendMessageForUserFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
class MessageHandler implements Handler {

    private final SendMessageForUserFactory sendMessageForUserFactory;
    private final UserService userService;
    private final UserWordListService userWordListService;

    private final Map<TextCommandsEnum, Consumer<BotEvent>> textCommandsHandler;

    MessageHandler(SendMessageForUserFactory sendMessageForUserFactory, UserService userService, UserWordListService userWordListService) {
        this.sendMessageForUserFactory = sendMessageForUserFactory;
        this.userService = userService;
        this.userWordListService = userWordListService;
        textCommandsHandler = new HashMap<>();
    }

    @Override
    public void handle(BotEvent botEvent) {
        TextCommandsEnum incomingCommand = TextCommandsEnum.fromString(botEvent.getText());

        var handlerMethod = textCommandsHandler.get(incomingCommand);
        handlerMethod.accept(botEvent);
    }

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

    private void handleDefault(BotEvent botEvent) {
    }

    private void handleDelete(BotEvent botEvent) {

    }

    private void handleStatistic(BotEvent botEvent) {

    }

    private void handleAddRandomWords(BotEvent botEvent) {

    }

    private void handleListRepetitionWords(BotEvent botEvent) {

    }

    private void handleListStudyWords(BotEvent botEvent) {

    }

    private void handleMixedMode(BotEvent botEvent) {

    }

    private void handleRepeatWord(BotEvent botEvent) {
    }


    private void handleLearnWord(BotEvent botEvent) {
        var messageSender = sendMessageForUserFactory.createMessageSender();
        var user = userService.getUserEntityFromDataBase(botEvent);
        var userWord = userWordListService.getRandomUserWordList(user, WordListTypeEnum.LEARNING);

        if (userWord == null) {
            messageSender.sendMessage(botEvent.getId(), "У вас нет слов для изучения в данный момент. Пожалуйста, " +
                    "добавьте новые слова, или воспользуйтесь нашим банком слов.");
        } else {
            //НАПИСАТЬ МЕТОДЫ ДЛЯ РАБОТЫ С ПРОИЗНОШЕНИЯМИ
            String messageText = userWordListService.getUserWordListString(userWord);
            messageSender.sendMessage(botEvent.getId(), messageText);
        }
    }

    private void handleAddWord(BotEvent botEvent) {
        userService.changeUserState(UserStateEnum.ADD_MENU, botEvent);
        sendMessageForUserFactory
                .createMessageSender()
                .sendMessage(botEvent.getId(), """
                        Можете отправлять слова, которые хотите добавить в свою коллекцию.\s

                        Если нужно добавить несколько слов, можете отправлять их по очереди.

                        Можете отправлять также словосочетания

                        Учтите, что слова переводятся автоматически, с помощью сервисов онлайн перевода и никак не проходят дополнительные проверки орфографии. Поэтому даже при небольших ошибках, перевод также будет ошибочный.""");

    }

    private void handleAnswer(BotEvent botEvent) {
        userService.changeUserState(UserStateEnum.ANSWER, botEvent);
        sendMessageForUserFactory
                .createMessageSender()
                .sendMessage(botEvent.getId(), "Пришлите пожалуйста ваш вопрос. \n\nПримечание: получение ответа может занять некоторое время");
    }


    private void handleStartAndHelp(BotEvent botEvent) {
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
    }
}
