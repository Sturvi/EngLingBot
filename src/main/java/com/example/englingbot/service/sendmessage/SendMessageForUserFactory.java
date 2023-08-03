package com.example.englingbot.service.sendmessage;

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
        return context.getBean(SendMessageForUser.class);
    }
}
