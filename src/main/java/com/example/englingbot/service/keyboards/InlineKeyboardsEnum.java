package com.example.englingbot.service.keyboards;

import java.util.function.Supplier;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public enum InlineKeyboardsEnum {
    YES_NO_KEYBOARD(InlineKeyboardMarkupFactory::getYesOrNoKeyboard),
    TRANSLATOR_KEYBOARD(InlineKeyboardMarkupFactory::getWordFromTranslatorKeyboard);

    private final Supplier<InlineKeyboardMarkup> keyboardSupplier;

    InlineKeyboardsEnum(Supplier<InlineKeyboardMarkup> keyboardSupplier) {
        this.keyboardSupplier = keyboardSupplier;
    }

    public InlineKeyboardMarkup getKeyboard() {
        return keyboardSupplier.get();
    }
}
