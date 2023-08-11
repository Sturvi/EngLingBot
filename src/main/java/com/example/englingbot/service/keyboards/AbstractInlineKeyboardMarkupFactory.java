package com.example.englingbot.service.keyboards;

import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract factory class for creating InlineKeyboardMarkup objects.
 */
@Slf4j
public abstract class AbstractInlineKeyboardMarkupFactory {

    /**
     * Creates a new InlineKeyboardMarkup object with an empty keyboard.
     *
     * @return new InlineKeyboardMarkup object.
     */
    protected static InlineKeyboardMarkup creatNewInlineKeyboard() {
        log.debug("Creating new empty InlineKeyboardMarkup");
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    /**
     * Adds a button to the InlineKeyboardMarkup object with the given parameters.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param keyboardDataEnum     Enum representing the data for the button.
     * @param identifierInData                 Word for the button text.
     * @param isNewLine            Whether to add the button to a new line.
     */
    protected static void addButton(InlineKeyboardMarkup inlineKeyboardMarkup, KeyboardDataEnum keyboardDataEnum, String identifierInData, boolean isNewLine) {
        var data = identifierInData != null ? keyboardDataEnum.getData() + " " + identifierInData : keyboardDataEnum.getData();
        var text = keyboardDataEnum.getText();
        log.debug("Adding button to {} line: text={}, data={}", isNewLine ? "new" : "current", text, data);
        var keyboardRoad = isNewLine ? getNewKeyboardRoad(inlineKeyboardMarkup) : getCurrentKeyboardRoad(inlineKeyboardMarkup);

        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(data);
        keyboardRoad.add(button);
    }

    /**
     * Adds a button to a new line in the InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param keyboardDataEnum     Enum representing the data for the button.
     * @param word                 Word for the button text.
     */
    protected static void addButtonToNewLine(InlineKeyboardMarkup inlineKeyboardMarkup, KeyboardDataEnum keyboardDataEnum, String word) {
        addButton(inlineKeyboardMarkup, keyboardDataEnum, word, true);
    }

    /**
     * Adds a button to a new line in the InlineKeyboardMarkup object without a specific word.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param keyboardDataEnum     Enum representing the data for the button.
     */
    protected static void addButtonToNewLine(InlineKeyboardMarkup inlineKeyboardMarkup, KeyboardDataEnum keyboardDataEnum) {
        addButton(inlineKeyboardMarkup, keyboardDataEnum, null, true);
    }

    /**
     * Adds a button to the current line in the InlineKeyboardMarkup object without a specific word.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param keyboardDataEnum     Enum representing the data for the button.
     */
    protected static void addButtonToCurrentLine(InlineKeyboardMarkup inlineKeyboardMarkup, KeyboardDataEnum keyboardDataEnum) {
        addButton(inlineKeyboardMarkup, keyboardDataEnum, null, false);
    }

    /**
     * Adds a button to the current line in the InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param keyboardDataEnum     Enum representing the data for the button.
     * @param word                 Word for the button text.
     */
    protected static void addButtonToCurrentLine(InlineKeyboardMarkup inlineKeyboardMarkup, KeyboardDataEnum keyboardDataEnum, String word) {
        addButton(inlineKeyboardMarkup, keyboardDataEnum, word, false);
    }

    /**
     * Gets a new keyboard row for the given InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to get the new row for.
     * @return new keyboard row.
     */
    private static List<InlineKeyboardButton> getNewKeyboardRoad(InlineKeyboardMarkup inlineKeyboardMarkup) {
        log.debug("Getting new keyboard row");
        List<InlineKeyboardButton> keyboardRoad = new ArrayList<>();
        inlineKeyboardMarkup.getKeyboard().add(keyboardRoad);
        return keyboardRoad;
    }

    /**
     * Gets the current keyboard row for the given InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to get the current row for.
     * @return current keyboard row.
     */
    private static List<InlineKeyboardButton> getCurrentKeyboardRoad(InlineKeyboardMarkup inlineKeyboardMarkup) {
        log.debug("Getting current keyboard row");
        var keyboard = inlineKeyboardMarkup.getKeyboard();
        return keyboard.get(keyboard.size() - 1);
    }
}
