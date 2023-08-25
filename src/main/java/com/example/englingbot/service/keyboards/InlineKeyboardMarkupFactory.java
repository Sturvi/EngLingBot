package com.example.englingbot.service.keyboards;

import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class InlineKeyboardMarkupFactory extends AbstractInlineKeyboardMarkupFactory {

    /**
     * Creates a keyboard with options for "Yes" or "No" with a specific identifierInData.
     *
     * @param identifierInData The specific identifierInData to be used.
     * @return A keyboard with "Yes" and "No" options.
     */
    public InlineKeyboardMarkup getYesOrNoKeyboard(String identifierInData) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.YES, identifierInData);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NO, identifierInData);

        return keyboard;
    }

    /**
     * Creates a keyboard with a identifierInData from the translator.
     *
     * @param identifierInData The specific identifierInData to be used.
     * @return A keyboard with the translated identifierInData.
     */
    public InlineKeyboardMarkup getWordFromTranslatorKeyboard(String identifierInData) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.TRANSLATOR, identifierInData);

        return keyboard;
    }

    /**
     * Creates a keyboard for the current identifierInData in the user's identifierInData list.
     *
     * @param userVocabulary The vocabulary object containing user data.
     * @param identifierInData           The specific identifierInData to be used.
     * @return A keyboard with options related to the current identifierInData in the user's identifierInData list.
     */
    public InlineKeyboardMarkup getKeyboardForCurrentWordInUserWordList(UserVocabulary userVocabulary, String identifierInData) {
        var timerValue = userVocabulary.getTimerValue();
        var keyboard = creatNewInlineKeyboard();

        if (timerValue > 7) {
            addButtonToNewLine(keyboard, KeyboardDataEnum.LEARNED, identifierInData);
        }

        addButtonToNewLine(keyboard, KeyboardDataEnum.USAGE_EXAMPLES, identifierInData);

        addButtonToNewLine(keyboard, KeyboardDataEnum.REMEMBERED, identifierInData);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.CONTEXT, identifierInData);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NOT_REMEMBERED, identifierInData);

        return keyboard;
    }

    /**
     * Creates a keyboard with a "Next" option.
     *
     * @return A keyboard with the "Next" option.
     */
    public InlineKeyboardMarkup getNextKeyboard() {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.NEXT);

        return keyboard;
    }
}
