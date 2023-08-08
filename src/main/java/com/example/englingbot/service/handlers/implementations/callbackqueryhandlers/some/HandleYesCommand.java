package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.englingbot.model.enums.UserStateEnum.ADD_MENU;
import static com.example.englingbot.model.enums.UserStateEnum.DELETE_MENU;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleYesCommand implements SomeCallbackQueryHandler {
    private final WordService wordService;
    private final MessageService messageService;
    private final UserVocabularyService userVocabularyService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.debug("Handling 'Yes' command for bot event: {}", botEvent);
        var userState = appUser.getUserState();

        if (userState == ADD_MENU) {
            var wordText = KeyboardDataEnum.getWord(botEvent.getData());
            var word = wordService.getWordByTextMessage(wordText);
            userVocabularyService.addWordToUserVocabulary(word, appUser);

            String newTextForMessage = "Слово: " + botEvent.getText() + " добавлено в Ваш словарь.";

            messageService
                    .editTextAndDeleteInlineKeyboard(botEvent, newTextForMessage);
        } else if (userState == DELETE_MENU) {
            // Handle delete menu logic here
        }
    }

    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.YES;
    }
}
