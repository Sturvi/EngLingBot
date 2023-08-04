package com.example.englingbot.service.message.edittextmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Lazy
public class EditMessageForUserFactory {

    private final ApplicationContext context;

    public EditMessageForUser createNewMessage() {
        return context.getBean(EditMessageForUser.class);
    }
}
