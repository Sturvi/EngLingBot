package com.example.englingbot.service.keyboards;

import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class InlineKeyboardMarkupFactory extends AbstractInlineKeyboardMarkupFactory{

    public static InlineKeyboardMarkup getYesOrNoKeyboard (){
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard,
                KeyboardDataEnum.YES.getText(),
                KeyboardDataEnum.YES.getData());

        addButtonToCurrentLine(keyboard,
                KeyboardDataEnum.NO.getText(),
                KeyboardDataEnum.NO.getData());

        return keyboard;
    }

    public static InlineKeyboardMarkup getWordFromTranslatorKeyboard (){
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard,
                KeyboardDataEnum.TRANSLATOR.getText(),
                KeyboardDataEnum.TRANSLATOR.getData());

        return keyboard;
    }
}
