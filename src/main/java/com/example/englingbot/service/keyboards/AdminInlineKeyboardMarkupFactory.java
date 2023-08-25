package com.example.englingbot.service.keyboards;

import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class AdminInlineKeyboardMarkupFactory extends InlineKeyboardMarkupFactory{

    public InlineKeyboardMarkup getYesNoAndReReviewKeyboard(String identifierInData){
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, KeyboardDataEnum.RE_REVIEW, identifierInData);
        addButtonToNewLine(keyboard, KeyboardDataEnum.YES, identifierInData);
        addButtonToCurrentLine(keyboard, KeyboardDataEnum.NO, identifierInData);

        return keyboard;
    }
}
