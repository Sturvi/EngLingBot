package com.example.englingbot.service.externalapi.telegram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BotEventTest {

    @Mock
    private Update updateMock;
    @Mock
    private Message messageMock;
    @Mock
    private CallbackQuery callbackQueryMock;
    @Mock
    private ChatMemberUpdated chatMemberMock;
    @Mock
    private User userMock;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTelegramObject_ShouldCreateBotEvent_WhenUpdateIsMessageWithText() {
        // Arrange
        when(updateMock.hasCallbackQuery()).thenReturn(false);
        when(updateMock.hasMessage()).thenReturn(true);
        when(updateMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getChatId()).thenReturn(1L);
        when(messageMock.getFrom()).thenReturn(userMock);
        when(userMock.getUserName()).thenReturn("user name");
        when(messageMock.getMessageId()).thenReturn(5);
        when(messageMock.getText()).thenReturn("test text");

        // Act
        BotEvent result = BotEvent.getTelegramObject(updateMock);

        // Assert
        assertEquals(result.getId(), messageMock.getChatId());
        assertEquals(result.getUserName(), userMock.getUserName());
        assertEquals(result.getText(), messageMock.getText());
        assertEquals(result.getMessageId(), messageMock.getMessageId());
        assertTrue(result.isMessage());
        assertFalse(result.isCallbackQuery());
        assertFalse(result.isDeactivationQuery());
        assertFalse(result.isContact());
    }


    @Test
    void getTelegramObject_ShouldCreateBotEvent_WhenUpdateIsCallbackWithData() {
        // Arrange
        when(updateMock.hasCallbackQuery()).thenReturn(true);
        when(updateMock.getCallbackQuery()).thenReturn(callbackQueryMock);
        when(callbackQueryMock.getMessage()).thenReturn(messageMock);
        when(callbackQueryMock.getFrom()).thenReturn(userMock);
        when(callbackQueryMock.getData()).thenReturn("test Data");
        when(callbackQueryMock.getFrom().getId()).thenReturn(1L);


        // Act
        BotEvent result = BotEvent.getTelegramObject(updateMock);

        // Assert
        assertFalse(result.isMessage());
        assertTrue(result.isCallbackQuery());
        assertFalse(result.isDeactivationQuery());
        assertEquals(result.getData(), callbackQueryMock.getData());
        assertEquals(result.getId(), userMock.getId());
    }

    @Test
    void getTelegramObject_ShouldCreateBotEvent_WhenUpdateIsDeactivationQuery() {
        // Arrange
        ChatMember newChatMember = Mockito.mock(ChatMember.class);
        when(updateMock.hasMyChatMember()).thenReturn(true);
        when(updateMock.getMyChatMember()).thenReturn(chatMemberMock);
        when(chatMemberMock.getNewChatMember()).thenReturn(newChatMember);
        when(newChatMember.getStatus()).thenReturn("kicked");
        when(chatMemberMock.getFrom()).thenReturn(userMock);
        when(userMock.getId()).thenReturn(1L);
        when(userMock.getUserName()).thenReturn("user name");


        // Act
        BotEvent result = BotEvent.getTelegramObject(updateMock);

        // Assert
        assertFalse(result.isMessage());
        assertFalse(result.isCallbackQuery());
        assertTrue(result.isDeactivationQuery());
        assertEquals(result.getId(), userMock.getId());
        assertEquals(result.getUserName(), userMock.getUserName());
    }
}
