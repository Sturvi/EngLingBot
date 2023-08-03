package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserWordList;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.WordListTypeEnum;
import com.example.englingbot.service.UserWordListService;
import com.example.englingbot.service.enums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.sendmessage.SendMessageForUser;
import com.example.englingbot.service.sendmessage.SendMessageForUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class MessageHandlerTests {

    @Spy
    @InjectMocks
    private MessageHandler messageHandler;

    @Mock
    private SendMessageForUserFactory sendMessageForUserFactory;

    @Mock
    private AppUser appUser;

    @Mock
    private UserWordListService userWordListService;
    @Mock
    private DefaultMessageHandler defaultMessageHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageHandler.init();

    }

    @Test
    void handleMessage_startCommand() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.START.getCommand());
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createNewMessage()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessageWithReplyKeyboard(anyLong(), anyString());

        messageHandler.handle(botEvent, appUser);

        verify(appUser, times(1)).setUserState(UserStateEnum.MAIN);
        verify(sendMessageForUserFactory, times(1)).createNewMessage();
        verify(sendMessageForUser, times(1)).sendMessageWithReplyKeyboard(eq(123L), anyString());
    }

    @Test
    void handleMessage_answerCommand() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.ANSWER.getCommand());
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createNewMessage()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessageWithReplyKeyboard(anyLong(), anyString());

        messageHandler.handle(botEvent, appUser);

        verify(appUser, times(1)).setUserState(UserStateEnum.ANSWER);
        verify(sendMessageForUserFactory, times(1)).createNewMessage();
        verify(sendMessageForUser, times(1)).sendMessageWithReplyKeyboard(eq(123L), anyString());
    }

    @Test
    void handleMessage_addWordCommand() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.ADD_WORD.getCommand());
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createNewMessage()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessageWithReplyKeyboard(anyLong(), anyString());

        messageHandler.handle(botEvent, appUser);

        verify(appUser, times(1)).setUserState(UserStateEnum.ADD_MENU);
        verify(sendMessageForUserFactory, times(1)).createNewMessage();
        verify(sendMessageForUser, times(1)).sendMessageWithReplyKeyboard(eq(123L), anyString());
    }

    @Test
    void handleMessage_learnWordCommand_noWordsToLearn() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.LEARN_WORD.getCommand());
        when(userWordListService.getRandomUserWordList(any(), eq(WordListTypeEnum.LEARNING))).thenReturn(null);
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createNewMessage()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessageWithReplyKeyboard(anyLong(), anyString());

        messageHandler.handle(botEvent, appUser);

        verify(userWordListService, times(1)).getRandomUserWordList(any(), eq(WordListTypeEnum.LEARNING));
        verify(sendMessageForUserFactory, times(1)).createNewMessage();
        verify(sendMessageForUser, times(1)).sendMessageWithReplyKeyboard(eq(123L), anyString());
    }

    @Test
    void handleMessage_learnWordCommand_hasWordsToLearn() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.LEARN_WORD.getCommand());
        var wordFromUserDictionary = mock(UserWordList.class);
        when(userWordListService.getRandomUserWordList(any(), eq(WordListTypeEnum.LEARNING))).thenReturn(wordFromUserDictionary);
        String messageText = "A word to send to the user.";
        when(userWordListService.getUserWordListString(wordFromUserDictionary)).thenReturn(messageText);
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createNewMessage()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessageWithReplyKeyboard(anyLong(), anyString());

        messageHandler.handle(botEvent, appUser);

        verify(userWordListService, times(1)).getRandomUserWordList(any(), eq(WordListTypeEnum.LEARNING));
        verify(sendMessageForUserFactory, times(1)).createNewMessage();
        verify(sendMessageForUser, times(1)).sendMessageWithReplyKeyboard(eq(123L), eq(messageText));
    }
}