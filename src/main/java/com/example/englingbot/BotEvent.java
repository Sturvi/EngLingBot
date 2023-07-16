package com.example.englingbot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.*;
import lombok.AccessLevel;

/**
 * Represents a bot event.
 * An instance of this class is created when an update is received from the Telegram API.
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotEvent {

    private boolean isMessage;
    private boolean isCallbackQuery;
    private boolean isContact;
    private boolean isDeactivationQuery;
    private Long id;
    private Integer messageId;
    private String text;
    private String data;
    private String userName;
    private User from;
    private Contact contact;
    private String phoneNumber;

    /**
     * Creates a BotEvent object from an Update object.
     * @param update the Update object received from the Telegram API.
     * @return the created BotEvent object.
     */
    public static BotEvent getTelegramObject(Update update) {
        log.debug("Creating BotEvent object from the Update object");
        BotEvent botEvent = new BotEvent();

        botEvent.isMessage = isMessageWithText(update);
        botEvent.isCallbackQuery = isCallbackWithData(update);
        botEvent.isDeactivationQuery = isDeactivationQuery(update);

        botEvent.initTelegramObject(update);

        log.debug("BotEvent object created successfully");
        return botEvent;
    }

    /**
     * Initializes the object for handling unsubscriptions.
     * @param chatMemberUpdated a ChatMemberUpdated object representing chat member status update.
     */
    private void initUnsubscriptionObject(ChatMemberUpdated chatMemberUpdated) {
        log.debug("Initializing unsubscription object");
        User user = chatMemberUpdated.getFrom();

        this.isContact = false;
        this.id = user.getId();
        this.from = user;
        this.userName = user.getUserName();
        log.debug("Unsubscription object initialized");
    }

    /**
     * Initializes the object depending on the type of update.
     * @param update the Update object received from the Telegram API.
     */
    private void initTelegramObject(Update update) {
        log.debug("Initializing telegram object based on update type");
        if (isMessage) {
            initMessageObject(update.getMessage());
        } else if (isCallbackQuery) {
            initCallbackQueryObject(update.getCallbackQuery());
        } else if (isDeactivationQuery) {
            initUnsubscriptionObject(update.getMyChatMember());
        }
        log.debug("Telegram object initialized");
    }

    /**
     * Initializes the object for handling messages.
     * @param message a Message object representing a user's message.
     */
    private void initMessageObject(Message message) {
        log.debug("Initializing message object");
        id = message.getChatId();
        userName = message.getFrom().getUserName();
        messageId = message.getMessageId();
        text = message.getText();
        from = message.getFrom();
        isContact = message.hasContact();
        contact = message.getContact();
        if (Boolean.TRUE.equals(isContact)) {
            phoneNumber = contact.getPhoneNumber();
        }
        log.debug("Message object initialized");
    }

    /**
     * Initializes the object for handling callback queries.
     * @param callbackQuery a CallbackQuery object representing a user's callback.
     */
    private void initCallbackQueryObject(CallbackQuery callbackQuery) {
        log.debug("Initializing callback query object");
        id = callbackQuery.getFrom().getId();
        userName = callbackQuery.getFrom().getUserName();
        messageId = callbackQuery.getMessage().getMessageId();
        text = callbackQuery.getMessage().getText();
        data = callbackQuery.getData();
        from = callbackQuery.getFrom();
        isContact = false;
        contact = null;
        phoneNumber = null;
        log.debug("Callback query object initialized");
    }

    /**
     * Checks if the update contains a text message.
     * @param update the Update object received from the Telegram API.
     * @return true if the update contains a text message, false otherwise.
     */
    private static boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage();
    }

    /**
     * Checks if the update contains a callback with data.
     * @param update the Update object received from the Telegram API.
     * @return true if the update contains a callback with data, false otherwise.
     */
    private static boolean isCallbackWithData(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();

        if (callbackQuery == null) {
            return false;
        }

        String data = callbackQuery.getData();
        return data != null && !data.isEmpty();
    }

    /**
     * Checks if the update contains a deactivation request.
     * @param update the Update object received from the Telegram API.
     * @return true if the update contains a deactivation request, false otherwise.
     */
    private static boolean isDeactivationQuery(Update update) {
        if (update.hasMyChatMember()) {
            return update.getMyChatMember().getNewChatMember().getStatus().equals("kicked");
        }
        return false;
    }
}
