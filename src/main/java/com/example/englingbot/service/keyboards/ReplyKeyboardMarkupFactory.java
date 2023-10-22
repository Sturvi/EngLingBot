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
     * Create a keyboard layout for normal users.
     *
     * @return A new ReplyKeyboardMarkup object designed for normal users.
     */
    public static ReplyKeyboardMarkup getUserReplyKeyboardMarkup() {
        log.trace("Generating user keyboard layout");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.ADD_WORD, UserTextCommandsEnum.ADD_RANDOM_WORDS)));
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.LIST_REPETITION_WORDS, UserTextCommandsEnum.LIST_STUDY_WORDS)));
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.REPEAT_WORD, UserTextCommandsEnum.MIXED_MODE, UserTextCommandsEnum.LEARN_WORD)));

        return createKeyboardMarkup(rows);
    }

    /**
     * Create a keyboard layout for admin users.
     *
     * @return A new ReplyKeyboardMarkup object designed for admin users.
     */
    public static ReplyKeyboardMarkup getAdminReplyKeyboardMarkup() {
        log.trace("Generating admin keyboard layout");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(createRow(EnumSet.of(AdminTextComandsEnum.REVIEW_NEW_WORD)));

        return createKeyboardMarkup(rows);
    }

    /**
     * Create a keyboard layout for tutor chat.
     *
     * @return A new ReplyKeyboardMarkup object designed for tutor chat.
     */
    public static ReplyKeyboardMarkup getTutorChatKeyboard() {
        log.trace("Generating tutor chat keyboard layout");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.NEW_CHAT)));
        rows.add(createRow(EnumSet.of(UserTextCommandsEnum.HOME)));

        return createKeyboardMarkup(rows);
    }


    /**
     * Creates a new ReplyKeyboardMarkup based on the provided list of KeyboardRows.
     *
     * @param keyboardRows List of KeyboardRow objects that define the keyboard layout.
     * @return A new ReplyKeyboardMarkup object.
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
     * Create a KeyboardRow based on the given set of commands.
     *
     * @param commands An EnumSet of commands to be added to the KeyboardRow.
     * @return A new KeyboardRow object.
     */
    private static KeyboardRow createRow(EnumSet<? extends Enum<?>> commands) {
        log.trace("Entering createRow method");

        KeyboardRow row = new KeyboardRow();
        commands.forEach(command -> row.add(new KeyboardButton(command.toString())));

        log.trace("Exiting createRow method");
        return row;
    }
}
