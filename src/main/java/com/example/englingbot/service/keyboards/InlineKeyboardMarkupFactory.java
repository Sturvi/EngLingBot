package com.example.englingbot.service.keyboards;

import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class InlineKeyboardMarkupFactory extends AbstractInlineKeyboardMarkupFactory {

    /**
     * Creates a keyboard with options for "Yes" or "No" with a specific word.
     *
     * @param word The specific word to be used.
     * @return A keyboard with "Yes" and "No" options.
     */
    public static InlineKeyboardMarkup getYesOrNoKeyboard(String word) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.YES, word);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NO, word);

        return keyboard;
    }

    /**
     * Creates a keyboard with a word from the translator.
     *
     * @param word The specific word to be used.
     * @return A keyboard with the translated word.
     */
    public static InlineKeyboardMarkup getWordFromTranslatorKeyboard(String word) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.TRANSLATOR, word);

        return keyboard;
    }

    /**
     * Creates a keyboard for the current word in the user's word list.
     *
     * @param userVocabulary The vocabulary object containing user data.
     * @param word           The specific word to be used.
     * @return A keyboard with options related to the current word in the user's word list.
     */
    public static InlineKeyboardMarkup getKeyboardForCurrentWordInUserWordList(UserVocabulary userVocabulary, String word) {
        var timerValue = userVocabulary.getTimerValue();
        var keyboard = creatNewInlineKeyboard();

        if (timerValue > 7) {
            addButtonToNewLine(keyboard, KeyboardDataEnum.LEARNED, word);
        }

        addButtonToNewLine(keyboard, KeyboardDataEnum.USAGEEXAMPLES, word);

        addButtonToNewLine(keyboard, KeyboardDataEnum.REMEMBERED, word);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.CONTEXT, word);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NOTREMEMBERED, word);

        return keyboard;
    }

    /**
     * Creates a keyboard with a "Next" option.
     *
     * @return A keyboard with the "Next" option.
     */
    public static InlineKeyboardMarkup getNextKeyboard() {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.NEXT);

        return keyboard;
    }
}
