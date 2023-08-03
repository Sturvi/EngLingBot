package com.example.englingbot.service.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class InlineKeyboardMarkupFactory extends AbstractInlineKeyboardMarkupFactory{

    public static InlineKeyboardMarkup getYesOrNoKeyboard (){
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, "✅", "yes");
        addButtonToCurrentLine(keyboard, "⛔️", "no");

        return keyboard;
    }
}
