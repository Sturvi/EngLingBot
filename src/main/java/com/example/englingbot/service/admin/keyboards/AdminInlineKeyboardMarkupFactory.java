package com.example.englingbot.service.admin.keyboards;

import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.keyboards.AbstractInlineKeyboardMarkupFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class AdminInlineKeyboardMarkupFactory extends AbstractInlineKeyboardMarkupFactory {

    public static InlineKeyboardMarkup getYesOrNoKeyboard (String identifierInData) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.YES, identifierInData);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NO, identifierInData);

        return keyboard;
    }
}
