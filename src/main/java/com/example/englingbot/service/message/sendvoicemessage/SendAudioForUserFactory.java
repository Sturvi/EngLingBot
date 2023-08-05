package com.example.englingbot.service.message.sendvoicemessage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Factory class to create new instances of SendAudioForUser.
 */
@Component
@RequiredArgsConstructor
@Lazy
public class SendAudioForUserFactory {

    private final ApplicationContext context;

    /**
     * Creates a new SendAudioForUser instance using the application context.
     *
     * @return a new SendAudioForUser instance
     */
    public SendAudioForUser createNewAudio() {
        return context.getBean(SendAudioForUser.class);
    }
}
