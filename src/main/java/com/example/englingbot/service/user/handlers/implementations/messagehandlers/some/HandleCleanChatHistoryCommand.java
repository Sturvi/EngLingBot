package com.example.englingbot.service.user.handlers.implementations.messagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Chat;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.comandsenums.UserTextCommandsEnum;
import com.example.englingbot.service.externalapi.openai.TutorService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.keyboards.ReplyKeyboardMarkupFactory;
import com.example.englingbot.service.message.TelegramMessageService;
import com.example.englingbot.service.user.handlers.interfaces.SomeMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Class for handling the command to clean chat history.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HandleCleanChatHistoryCommand implements SomeMessageHandler {

    private final TutorService tutorService;
    private final TelegramMessageService telegramMessageService;

    /**
     * Handles a bot event to clean chat history.
     *
     * @param botEvent Contains the event data.
     * @param appUser The application user who initiated the bot event.
     */
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Entering handle method for BotEvent: {}", botEvent);

        appUser.setUserState(UserStateEnum.CHAT_WITH_TUTOR);
        log.debug("User state set to CHAT_WITH_TUTOR for AppUser: {}", appUser);

        var keyboard = ReplyKeyboardMarkupFactory.getTutorChatKeyboard();
        Chat chat = tutorService.createNewChat(appUser);
        log.debug("New Chat created: {}", chat);

        String messageText = chat.getMessages().get(chat.getMessages().size() - 1).getContent();
        log.trace("Last message content: {}", messageText);

        telegramMessageService.sendMessageWithKeyboard(
                botEvent.getId(),
                messageText,
                keyboard);
        log.debug("Message sent with BotEvent ID: {}", botEvent.getId());
    }

    /**
     * Specifies the command type for which this handler is available.
     *
     * @return The command type available for this handler.
     */
    @Override
    public UserTextCommandsEnum availableFor() {
        return UserTextCommandsEnum.NEW_CHAT;
    }
}
