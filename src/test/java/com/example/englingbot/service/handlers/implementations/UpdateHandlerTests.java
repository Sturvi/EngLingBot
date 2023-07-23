package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.UserService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateHandlerTests {
    @Mock
    private BotEvent botEvent;
    @Mock
    private UserService userService;
    @Mock
    private MessageHandler messageHandler;
    @Mock
    private CallbackQueryHandler callbackQueryHandler;

    private UpdateHandler updateHandler;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        User user = mock(User.class);
        when(botEvent.getFrom()).thenReturn(user);
        AppUser appUser = mock(AppUser.class);
        when(userService.saveOrUpdateAppUser(user)).thenReturn(appUser);
        updateHandler = new UpdateHandler(userService, messageHandler, callbackQueryHandler);
    }

    @Test
    void testHandleWhenBotEventIsMessage () {
        when(botEvent.isDeactivationQuery()).thenReturn(false);
        when(botEvent.isMessage()).thenReturn(true);

        updateHandler.handle(botEvent);

        verify(messageHandler, times(1)).handle(botEvent);
        verify(userService, never()).deactivateAppUser(any(BotEvent.class));
        verify(callbackQueryHandler, never()).handle(any(BotEvent.class));
    }

    @Test
    void testHandleWhenBotEventIsDeactivationQuery (){
        when(botEvent.isDeactivationQuery()).thenReturn(true);

        updateHandler.handle(botEvent);

        verify(userService, times(1)).deactivateAppUser(botEvent);
        verify(messageHandler, never()).handle(any(BotEvent.class));
        verify(callbackQueryHandler, never()).handle(any(BotEvent.class));
    }

    @Test
    void testHandleWhenBotEventIsCallbackQuery (){
        when(botEvent.isDeactivationQuery()).thenReturn(false);
        when(botEvent.isMessage()).thenReturn(false);
        when(botEvent.isCallbackQuery()).thenReturn(true);

        updateHandler.handle(botEvent);

        verify(callbackQueryHandler, times(1)).handle(botEvent);
        verify(userService, never()).deactivateAppUser(any(BotEvent.class));
        verify(messageHandler, never()).handle(any(BotEvent.class));
    }
}
