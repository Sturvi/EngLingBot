package com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jdbc.WorkExecutor;
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
    private final TemplateMessagesSender templateMessagesSender;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Handling bot event for user: {}", appUser.getId());

        var userState = appUser.getUserState();

        if (userState == ADD_MENU) {
            log.trace("User state is ADD_MENU");

            var wordText = KeyboardDataEnum.getWord(botEvent.getData());
            var wordOptional = wordService.getWordByTextMessage(wordText);
            Word word;

            if (wordOptional.isPresent()){
                word = wordOptional.get();
                log.debug("Successfully retrieved word: {}", wordText);
            } else {
                log.warn("Unable to find word for text: {}", wordText);
                templateMessagesSender.sendErrorMessage(botEvent.getId());
                return;
            }

            userVocabularyService.addWordToUserVocabulary(word, appUser);
            log.debug("Added word to user's vocabulary: {}", wordText);

            String newTextForMessage = "Слово: " + botEvent.getText() + " добавлено в Ваш словарь.";

            messageService.editTextAndDeleteInlineKeyboard(botEvent, newTextForMessage);
        } else if (userState == DELETE_MENU) {
            log.trace("User state is DELETE_MENU");
            // Handle delete menu logic here
        }
    }


    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.YES;
    }
}
