package com.example.englingbot.service.message.sendtextmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * The MessageSenderFactory class provides methods for creating instances of the MessageSender class.
 */
@Component
@RequiredArgsConstructor
@Lazy
public class SendMessageForUserFactory {

    private final ApplicationContext context;

    /**
     * Creates a new instance of MessageSender.
     *
     * @return A new instance of MessageSender.
     */
    public SendMessageForUser createNewMessage() {
        // TODO В бизнесовую логику замешался небизнесовый код, это не хорошо,
        //  стоит как-то по-другому отправлять сообщение пользователю
        return context.getBean(SendMessageForUser.class);
    }
}
