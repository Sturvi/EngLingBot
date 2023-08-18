package com.example.englingbot.service.message;

import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.voice.WordSpeaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

/**
 * A class responsible for sending template messages to users.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TemplateMessagesSender {
    private final MessageService messageService;
    private final WordSpeaker wordSpeaker;

    /**
     * Sends a start and help message to the specified chat.
     * @param chatId The ID of the chat to send the message to.
     */
    public void sendStartAndHelpMessage(Long chatId) {
        log.debug("Sending start and help message to chat ID {}", chatId);
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
        messageService.sendMessageToUser(chatId, startAndHelpMessage);
    }

    /**
     * Sends an "Add Word" message to the specified chat.
     * @param chatId The ID of the chat to send the message to.
     */
    public void sendAddWordMessage(Long chatId) {
        log.debug("Sending add word message to chat ID {}", chatId);
        String message = """
                Можете отправлять слова, которые хотите добавить в свою коллекцию.

                Если нужно добавить несколько слов, можете отправлять их по очереди.

                Можете отправлять также словосочетания

                Учтите, что слова переводятся автоматически, с помощью сервисов онлайн перевода и никак не проходят дополнительные проверки орфографии. Поэтому даже при небольших ошибках, перевод также будет ошибочный.""";
        messageService.sendMessageToUser(chatId, message);
    }

    /**
     * Sends a message indicating that there are no words to send for the specified user word state(s).
     * @param chatId The ID of the chat to send the message to.
     * @param types The types of word states.
     */
    public void sendNoWordToSendMessage(Long chatId, UserWordState... types) {
        log.debug("Sending no word to send message to chat ID {}, types {}", chatId, types);
        if (types.length == 1) {
            if (types[0] == UserWordState.LEARNING) {
                messageService.sendMessageToUser(
                        chatId,
                        "У вас нет слов для изучения в данный момент. Пожалуйста, " +
                                "добавьте новые слова, или воспользуйтесь нашим банком слов.");
            } else if (types[0] == UserWordState.REPETITION) {
                messageService.sendMessageToUser(
                        chatId,
                        "У вас нет слов на повторении в данный момент. Пожалуйста, " +
                                "воспользуйтесь меню \"\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDF93 Учить слова\"");
            }
        } else if (types.length == 2
                && Arrays.asList(types).contains(UserWordState.LEARNING)
                && Arrays.asList(types).contains(UserWordState.REPETITION)) {
            messageService.sendMessageToUser(
                    chatId,
                    "У вас нет слов для изучения или повторения в данный момент. Пожалуйста, " +
                            "добавьте новые слова, или воспользуйтесь нашим банком слов.");
        }
    }

    /**
     * Sends an error message to the specified chat.
     * @param chatId The ID of the chat to send the message to.
     */
    public void sendErrorMessage(Long chatId) {
        log.error("Sending error message to chat ID {}", chatId);
        messageService.sendMessageToUser(chatId, "Произошла непредвиденная ошибка. Постараемся решить ее в ближайшее время!");
    }

    public void sendAudioWithWord(Long chatId, UserVocabulary userWord, String messageText) {
        log.trace("Entering sendAudioWithWord method");

        Optional<File> audio = wordSpeaker.getVoice(userWord.getWord());

        if (audio.isPresent()) {
            log.debug("Audio file found for word: {}", userWord.getWord());
            messageService.sendAudio(chatId, "Произношение слова", audio.get());
        } else {
            log.warn("Audio file not found for word: {}", userWord.getWord());
        }

        var keyboard = InlineKeyboardMarkupFactory.getKeyboardForCurrentWordInUserWordList(userWord, userWord.getWord().getId().toString());

        messageService.sendMessageWithKeyboard(chatId, messageText, keyboard);

        log.trace("Exiting sendAudioWithWord method");

    }
}
