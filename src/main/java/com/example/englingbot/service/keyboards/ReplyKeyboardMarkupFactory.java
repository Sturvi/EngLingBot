package com.example.englingbot.service.keyboards;

import com.example.englingbot.service.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * ReplyKeyboardMarkupFactory is a class responsible for creating a predefined keyboard markup for Telegram bot.
 */
@Slf4j
public class ReplyKeyboardMarkupFactory {

    /**
     * Creates a ReplyKeyboardMarkup object which holds the keyboard layout to be shown to the Telegram users.
     *
     * @return a ReplyKeyboardMarkup object representing a specific keyboard layout.
     */
    public static ReplyKeyboardMarkup getUserReplyKeyboardMarkup() {
        log.debug("Start creating a new keyboard markup");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton(UserTextCommandsEnum.ADD_WORD.getCommand()));
        keyboardFirstRow.add(new KeyboardButton(UserTextCommandsEnum.ADD_RANDOM_WORDS.getCommand()));

        keyboardSecondRow.add(new KeyboardButton(UserTextCommandsEnum.LIST_REPETITION_WORDS.getCommand()));
        keyboardSecondRow.add(new KeyboardButton(UserTextCommandsEnum.LIST_STUDY_WORDS.getCommand()));

        keyboardThirdRow.add(new KeyboardButton(UserTextCommandsEnum.REPEAT_WORD.getCommand()));
        keyboardThirdRow.add(new KeyboardButton(UserTextCommandsEnum.MIXED_MODE.getCommand()));
        keyboardThirdRow.add(new KeyboardButton(UserTextCommandsEnum.LEARN_WORD.getCommand()));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        keyboardRowList.add(keyboardThirdRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        log.debug("Successfully created a new keyboard markup");

        try {
            return replyKeyboardMarkup;
        } catch (Exception e) {
            log.error("An error occurred while creating the keyboard markup", e);
            throw e;
        }
    }

    public static ReplyKeyboardMarkup getAdminReplyKeyboardMarkup() {
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
