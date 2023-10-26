package com.example.englingbot.service.keyboards;

import com.example.englingbot.service.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
    private static <T extends Enum<T>> KeyboardRow createRow(EnumSet<T> commands) {
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
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.ADD_WORD, UserTextCommandsEnum.ADD_RANDOM_WORDS)));
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.LIST_REPETITION_WORDS, UserTextCommandsEnum.LIST_STUDY_WORDS)));
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.REPEAT_WORD, UserTextCommandsEnum.MIXED_MODE, UserTextCommandsEnum.LEARN_WORD)));

        return createKeyboardMarkup(rows);
    }

    /**
     * Creates a ReplyKeyboardMarkup for admin users.
     * @return ReplyKeyboardMarkup object.
     */
    public static ReplyKeyboardMarkup getAdminReplyKeyboardMarkup() {
        log.info("Generating admin keyboard layout");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(createRow(EnumSet.of(AdminTextComandsEnum.REVIEW_NEW_WORD)));

        return createKeyboardMarkup(rows);
    }

    /**
     * Creates a ReplyKeyboardMarkup for tutor chat.
     * @return ReplyKeyboardMarkup object.
     */
    public static ReplyKeyboardMarkup getTutorChatKeyboard() {
        log.info("Generating tutor chat keyboard layout");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.NEW_CHAT)));
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.HOME)));

        return createKeyboardMarkup(rows);
    }
}
