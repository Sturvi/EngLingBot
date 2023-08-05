package com.example.englingbot.service.message;

import com.example.englingbot.model.enums.UserWordState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TemplateMessagesSender {
    private final MessageService messageService;

    public void sendStartAndHelpMessage(Long chatId) {
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

        messageService.sendMessage(chatId, startAndHelpMessage);
    }

    public void sendAddWordMessage(Long chatId) {
        String message = """
                Можете отправлять слова, которые хотите добавить в свою коллекцию.\\s

                Если нужно добавить несколько слов, можете отправлять их по очереди.

                Можете отправлять также словосочетания

                Учтите, что слова переводятся автоматически, с помощью сервисов онлайн перевода и никак не проходят дополнительные проверки орфографии. Поэтому даже при небольших ошибках, перевод также будет ошибочный.""";
        messageService.sendMessage(chatId, message);
    }

    public void sendNoWordToSendMessage(Long chatId, UserWordState... types) {
        if (types.length == 1) {
            if (types[0] == UserWordState.LEARNING) {
                messageService.sendMessage(
                        chatId,
                        "У вас нет слов для изучения в данный момент. Пожалуйста, " +
                                "добавьте новые слова, или воспользуйтесь нашим банком слов.");
            } else if (types[0] == UserWordState.REPETITION) {
                messageService.sendMessage(
                        chatId,
                        "У вас нет слов на повторении в данный момент. Пожалуйста, " +
                                "воспользуйтесь меню \"\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDF93 Учить слова\"");
            }
        } else if (types.length == 2
                && Arrays.asList(types).contains(UserWordState.LEARNING)
                && Arrays.asList(types).contains(UserWordState.REPETITION)) {
            messageService.sendMessage(
                    chatId,
                    "У вас нет слов для изучения или повторения в данный момент. Пожалуйста, " +
                            "добавьте новые слова, или воспользуйтесь нашим банком слов.");
        }
    }

    public void sendErrorMessage(Long chatId) {
        messageService.sendMessage(chatId,
                "Произошла непредвиденная ошибка. Постараемся решить ее в ближайшее время!");
    }
}
