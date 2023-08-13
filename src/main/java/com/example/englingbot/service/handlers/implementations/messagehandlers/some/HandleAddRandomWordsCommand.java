package com.example.englingbot.service.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeMessageHandler;
import com.example.englingbot.service.keyboards.InlineKeyboardMarkupFactory;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HandleAddRandomWordsCommand implements SomeMessageHandler {
    private final WordService wordService;
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        appUser.setUserState(UserStateEnum.ADD_MENU);
        var newWordList = wordService.getTenRandomNewWord(appUser);

        for (Word word :
                newWordList) {
            var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard(word.getId().toString());
            messageService.sendMessageWithKeyboard(botEvent.getId(), word.toString(), keyboard);
        }
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.ADD_RANDOM_WORDS;
    }
}
