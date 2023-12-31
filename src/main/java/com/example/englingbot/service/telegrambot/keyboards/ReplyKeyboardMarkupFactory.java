package com.example.englingbot.service.telegrambot.keyboards;

import com.example.englingbot.service.telegrambot.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.telegrambot.comandsenums.UserTextCommandsEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

/**
 * ReplyKeyboardMarkupFactory is a class responsible for creating
 * predefined keyboard markups for a Telegram bot.
 */
@Slf4j
public class ReplyKeyboardMarkupFactory {

    /**
     * Creates a new ReplyKeyboardMarkup based on the provided list of KeyboardRows.
     * @param keyboardRows List of KeyboardRow objects.
     * @return ReplyKeyboardMarkup object.
     */
    private static ReplyKeyboardMarkup createKeyboardMarkup(List<KeyboardRow> keyboardRows) {
        log.trace("Entering createKeyboardMarkup method");
        log.debug("Start creating a new keyboard markup");

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        markup.setKeyboard(keyboardRows);

        log.debug("Successfully created a new keyboard markup");
        log.trace("Exiting createKeyboardMarkup method");

        return markup;
    }

    /**
     * Creates a KeyboardRow based on the given EnumSet of commands.
     * @param commands EnumSet of commands.
     * @return KeyboardRow object.
     */
    private static <T extends Enum<T>> KeyboardRow createRow(List<T> commands) {
        log.trace("Entering createRow method");

        KeyboardRow row = new KeyboardRow();
        for (T command : commands) {
            if (command instanceof UserTextCommandsEnum) {
                row.add(new KeyboardButton(((UserTextCommandsEnum) command).getCommand()));
            } else if (command instanceof AdminTextComandsEnum) {
                row.add(new KeyboardButton(((AdminTextComandsEnum) command).getCommand()));
            }
        }

        log.trace("Exiting createRow method");
        return row;
    }

    /**
     * Creates a ReplyKeyboardMarkup for normal users.
     * @return ReplyKeyboardMarkup object.
     */
    public static ReplyKeyboardMarkup getUserReplyKeyboardMarkup() {
        log.info("Generating user keyboard layout");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(createRow(List.of(UserTextCommandsEnum.CHAT_WITH_TUTOR)));
        rows.add(createRow(List.of(UserTextCommandsEnum.ADD_WORD)));
        rows.add(createRow(List.of(UserTextCommandsEnum.LEARN_WORD)));

        return createKeyboardMarkup(rows);
    }

    /**
     * Creates a ReplyKeyboardMarkup for admin users.
     * @return ReplyKeyboardMarkup object.
     */
    public static ReplyKeyboardMarkup getAdminReplyKeyboardMarkup() {
        log.info("Generating admin keyboard layout");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(createRow(List.of(AdminTextComandsEnum.REVIEW_NEW_WORD)));

        return createKeyboardMarkup(rows);
    }

    /**
     * Creates a ReplyKeyboardMarkup for tutor chat.
     * @return ReplyKeyboardMarkup object.
     */
    public static ReplyKeyboardMarkup getTutorChatKeyboard() {
        log.info("Generating tutor chat keyboard layout");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(createRow(List.of(UserTextCommandsEnum.NEW_CHAT)));
        rows.add(createRow(List.of(UserTextCommandsEnum.HOME)));

        return createKeyboardMarkup(rows);
    }
}
