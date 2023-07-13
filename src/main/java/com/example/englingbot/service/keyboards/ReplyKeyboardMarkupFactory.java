package com.example.englingbot.service.keyboards;

import com.example.englingbot.service.enums.TextCommandsEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ReplyKeyboardMarkupFactory {

    public static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton(TextCommandsEnum.ADD_WORD.getCommand()));
        keyboardFirstRow.add(new KeyboardButton(TextCommandsEnum.ADD_RANDOM_WORDS.getCommand()));

        keyboardSecondRow.add(new KeyboardButton(TextCommandsEnum.LIST_REPETITION_WORDS.getCommand()));
        keyboardSecondRow.add(new KeyboardButton(TextCommandsEnum.LIST_STUDY_WORDS.getCommand()));

        keyboardThirdRow.add(new KeyboardButton(TextCommandsEnum.REPEAT_WORD.getCommand()));
        keyboardThirdRow.add(new KeyboardButton(TextCommandsEnum.MIXED_MODE.getCommand()));
        keyboardThirdRow.add(new KeyboardButton(TextCommandsEnum.LEARN_WORD.getCommand()));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        keyboardRowList.add(keyboardThirdRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
    }
}
