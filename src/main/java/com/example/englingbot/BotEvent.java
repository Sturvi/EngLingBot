package com.example.englingbot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.*;
import lombok.AccessLevel;

/**
 * Класс, представляющий событие бота.
 * Экземпляр этого класса создается при получении обновления от Telegram API.
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
     * Создает объект BotEvent из объекта Update.
     * @param update объект Update, полученный от Telegram API.
     * @return созданный объект BotEvent.
     */
    public static BotEvent getTelegramObject(Update update) {
        BotEvent botEvent = new BotEvent();

        botEvent.isMessage = isMessageWithText(update);
        botEvent.isCallbackQuery = isCallbackWithData(update);
        botEvent.isDeactivationQuery = isDeactivationQuery(update);

        botEvent.initTelegramObject(update);

        return botEvent;
    }

    /**
     * Инициализирует объект для обработки отписки.
     * @param chatMemberUpdated объект ChatMemberUpdated, представляющий обновление статуса участника чата.
     */
    private void initUnsubscriptionObject(ChatMemberUpdated chatMemberUpdated) {
        User user = chatMemberUpdated.getFrom();

        this.isContact = false;
        this.id = user.getId();
        this.from = user;
        this.userName = user.getUserName();
    }

    /**
     * Инициализирует объект в зависимости от типа обновления.
     * @param update объект Update, полученный от Telegram API.
     */
    private void initTelegramObject(Update update) {
        if (isMessage) {
            initMessageObject(update.getMessage());
        } else if (isCallbackQuery) {
            initCallbackQueryObject(update.getCallbackQuery());
        } else if (isDeactivationQuery) {
            initUnsubscriptionObject(update.getMyChatMember());
        }
    }

    /**
     * Инициализирует объект для обработки сообщений.
     * @param message объект Message, представляющий сообщение от пользователя.
     */
    private void initMessageObject(Message message) {
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
    }

    /**
     * Инициализирует объект для обработки обратных вызовов.
     * @param callbackQuery объект CallbackQuery, представляющий обратный вызов от пользователя.
     */
    private void initCallbackQueryObject(CallbackQuery callbackQuery) {
        id = callbackQuery.getFrom().getId();
        userName = callbackQuery.getFrom().getUserName();
        messageId = callbackQuery.getMessage().getMessageId();
        text = callbackQuery.getMessage().getText();
        data = callbackQuery.getData();
        from = callbackQuery.getFrom();
        isContact = false;
        contact = null;
        phoneNumber = null;
    }

    /**
     * Проверяет, содержит ли обновление текстовое сообщение.
     * @param update объект Update, полученный от Telegram API.
     * @return true, если обновление содержит текстовое сообщение, иначе false.
     */
    private static boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage();
    }

    /**
     * Проверяет, содержит ли обновление обратный вызов с данными.
     * @param update объект Update, полученный от Telegram API.
     * @return true, если обновление содержит обратный вызов с данными, иначе false.
     */
    private static boolean isCallbackWithData(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();

        if (callbackQuery == null) {
            return false;
        }

        String data = callbackQuery.getData();
        boolean hasData = data != null && !data.isEmpty();

        return hasData;
    }

    /**
     * Проверяет, содержит ли обновление запрос на деактивацию.
     * @param update объект Update, полученный от Telegram API.
     * @return true, если обновление содержит запрос на деактивацию, иначе false.
     */
    private static boolean isDeactivationQuery(Update update) {
        if (update.hasMyChatMember()) {
            return update.getMyChatMember().getNewChatMember().getStatus().equals("kicked");
        }
        return false;
    }
}
