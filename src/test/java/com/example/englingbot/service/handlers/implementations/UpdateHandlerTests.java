package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.AppUserService;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.implementations.callbackqueryhandlers.CallbackQueryHandler;
import com.example.englingbot.service.handlers.implementations.messagehandlers.MessageHandler;
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
    private AppUserService appUserService;
    @Mock
    private MessageHandler messageHandler;
    @Mock
    private CallbackQueryHandler callbackQueryHandler;
    @Mock
    private AppUser appUser;

    private UpdateHandler updateHandler;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        User user = mock(User.class);
        when(botEvent.getFrom()).thenReturn(user);
        AppUser appUser = mock(AppUser.class);
        when(appUserService.saveOrUpdateAppUser(user)).thenReturn(appUser);
        updateHandler = new UpdateHandler(messageHandler, callbackQueryHandler);
    }

    @Test
    void testHandleWhenBotEventIsMessage () {
        when(botEvent.isDeactivationQuery()).thenReturn(false);
        when(botEvent.isMessage()).thenReturn(true);

        updateHandler.handle(botEvent, appUser);

        verify(messageHandler, times(1)).handle(botEvent, appUser);
        verify(appUser, never()).setUserStatus(false);
        verify(callbackQueryHandler, never()).handle(botEvent, appUser);
    }

    @Test
    void testHandleWhenBotEventIsDeactivationQuery (){
        when(botEvent.isDeactivationQuery()).thenReturn(true);

        updateHandler.handle(botEvent, appUser);

        verify(appUser, times(1)).setUserStatus(false);
        verify(messageHandler, never()).handle(botEvent, appUser);
        verify(callbackQueryHandler, never()).handle(botEvent, appUser);
    }

    @Test
    void testHandleWhenBotEventIsCallbackQuery (){
        when(botEvent.isDeactivationQuery()).thenReturn(false);
        when(botEvent.isMessage()).thenReturn(false);
        when(botEvent.isCallbackQuery()).thenReturn(true);

        updateHandler.handle(botEvent, appUser);

        verify(callbackQueryHandler, times(1)).handle(botEvent, appUser);
        verify(appUser, never()).setUserStatus(false);
        verify(messageHandler, never()).handle(botEvent, appUser);
    }
}
