package com.example.englingbot.service.admin.keyboards;

import com.example.englingbot.service.admin.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AdminReplyKeyboardFactory {

    public static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        log.debug("Start creating a new keyboard markup for admin");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton(AdminTextComandsEnum.REVIEW_NEW_WORD.getCommand()));

        keyboardRowList.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        log.debug("Successfully created a new keyboard markup");

        try {
            return replyKeyboardMarkup;
        } catch (Exception e) {
            log.error("An error occurred while creating the keyboard markup", e);
            throw e;
        }
    }
}
