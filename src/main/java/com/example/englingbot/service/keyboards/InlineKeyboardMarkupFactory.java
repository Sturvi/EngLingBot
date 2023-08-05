package com.example.englingbot.service.keyboards;

import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class InlineKeyboardMarkupFactory extends AbstractInlineKeyboardMarkupFactory {

    public static InlineKeyboardMarkup getYesOrNoKeyboard(String word) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.YES, word);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NO, word);

        return keyboard;
    }

    public static InlineKeyboardMarkup getWordFromTranslatorKeyboard(String word) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.TRANSLATOR, word);

        return keyboard;
    }

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

    public static InlineKeyboardMarkup getNextKeyboard() {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.NEXT);

        return keyboard;
    }
}
