package com.example.englingbot.service.user.handlers.implementations.callbackqueryhandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.WordService;
import com.example.englingbot.service.comandsenums.KeyboardDataEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.user.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        var word = getWordFromEvent(botEvent);
        if (word == null) {
            return;
        }

        switch (appUser.getUserState()) {
            case ADD_MENU:
                processUserWordAndSendResponse(word, botEvent, appUser, true);
                break;
            case DELETE_MENU:
                processUserWordAndSendResponse(word, botEvent, appUser, false);
                break;
        }
    }

    private Word getWordFromEvent(BotEvent botEvent) {
        var wordId = KeyboardDataEnum.getWordId(botEvent.getData());
        var wordOptional = wordService.getWord(wordId);

        if (wordOptional.isPresent()) {
            log.debug("Successfully retrieved word: {}", wordId);
            return wordOptional.get();
        } else {
            log.warn("Unable to find word for text: {}", wordId);
            templateMessagesSender.sendErrorMessage(botEvent.getId());
            return null;
        }
    }

    private void processUserWordAndSendResponse(Word word, BotEvent botEvent, AppUser appUser, boolean isAdd) {
        String action = isAdd ? "Added" : "Deleted";
        log.trace("User state is {}", isAdd ? "ADD_MENU" : "DELETE_MENU");

        if (isAdd) {
            userVocabularyService.addWordToUserVocabulary(word, appUser);
        } else {
            userVocabularyService.deleteWordFromUserVocabulary(word, appUser);
        }

        log.debug("{} word to user's vocabulary: {}", action, word.toString());

        String newTextForMessage = "Слово: " + word.toString() + (isAdd ? " добавлено в Ваш словарь." : " удалено из вашего словаря.");
        messageService.editTextAndDeleteInlineKeyboard(botEvent, newTextForMessage);
    }



    @Override
    public KeyboardDataEnum availableFor() {
        return KeyboardDataEnum.YES;
    }
}
