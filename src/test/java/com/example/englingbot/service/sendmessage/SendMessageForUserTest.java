package com.example.englingbot.service.sendmessage;

import com.example.englingbot.service.externalapi.telegram.TelegramBotApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.mockito.Mockito.*;

public class SendMessageForUserTest {

    @Mock
    private TelegramBotApplication telegramBotApplication;

    @InjectMocks
    private SendMessageForUser sendMessageForUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sendMessageForUser = new SendMessageForUser(telegramBotApplication);
    }

    @Test
    void testSendMessage() throws Exception {
        Long chatId = 123L;
        String messageText = "test message";
        Message returnedMessage = mock(Message.class);

        when(telegramBotApplication.execute(any(SendMessage.class))).thenReturn(returnedMessage);

        sendMessageForUser.sendMessageWithReplyKeyboard(chatId, messageText);

        verify(telegramBotApplication, times(1)).execute(any(SendMessage.class));
    }
}