package com.example.englingbot.service.keyboards;

import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class InlineKeyboardMarkupFactory extends AbstractInlineKeyboardMarkupFactory {

    public static InlineKeyboardMarkup getYesOrNoKeyboard() {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.YES);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NO);

        return keyboard;
    }

    public static InlineKeyboardMarkup getWordFromTranslatorKeyboard() {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.TRANSLATOR);

        return keyboard;
    }

    public static InlineKeyboardMarkup getKeyboardForCurrentWordInUserWordList(UserVocabulary userVocabulary) {
        var timerValue = userVocabulary.getTimerValue();
        var keyboard = creatNewInlineKeyboard();

        if (timerValue > 7) {
            addButtonToNewLine(keyboard, KeyboardDataEnum.LEARNED);
        }

        addButtonToNewLine(keyboard, KeyboardDataEnum.USAGEEXAMPLES);

        addButtonToNewLine(keyboard, KeyboardDataEnum.REMEMBERED);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.CONTEXT);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NOTREMEMBERED);

        return keyboard;
    }
}
