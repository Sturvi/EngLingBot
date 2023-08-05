package com.example.englingbot.service.message.edittextmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Factory class to create new instances of EditMessageForUser.
 */
@Component
@RequiredArgsConstructor
@Lazy
public class EditMessageForUserFactory {

    private final ApplicationContext context;

    /**
     * Creates a new EditMessageForUser instance using the application context.
     *
     * @return a new EditMessageForUser instance
     */
    public EditMessageForUser createNewMessage() {
        return context.getBean(EditMessageForUser.class);
    }
}
