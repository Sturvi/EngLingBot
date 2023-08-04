package com.example.englingbot.service.message.sendvoicemessage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Lazy
public class SendAudioForUserFactory {

    private final ApplicationContext context;

    public SendAudioForUser createNewAudio() {
        return context.getBean(SendAudioForUser.class);
    }
}
