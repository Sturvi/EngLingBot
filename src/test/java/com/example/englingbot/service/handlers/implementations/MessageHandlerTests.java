package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.UserWordList;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.WordListTypeEnum;
import com.example.englingbot.service.UserService;
import com.example.englingbot.service.UserWordListService;
import com.example.englingbot.service.enums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.sendmessage.SendMessageForUser;
import com.example.englingbot.service.sendmessage.SendMessageForUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class MessageHandlerTest {

    @Spy
    @InjectMocks
    private MessageHandler messageHandler;

    @Mock
    private SendMessageForUserFactory sendMessageForUserFactory;

    @Mock
    private UserService userService;

    @Mock
    private UserWordListService userWordListService;

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
        when(sendMessageForUserFactory.createMessageSender()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessage(anyLong(), anyString());

        messageHandler.handle(botEvent);

        verify(userService, times(1)).changeAppUserState(UserStateEnum.MAIN, botEvent);
        verify(sendMessageForUserFactory, times(1)).createMessageSender();
        verify(sendMessageForUser, times(1)).sendMessage(eq(123L), anyString());
    }

    @Test
    void handleMessage_answerCommand() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.ANSWER.getCommand());
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createMessageSender()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessage(anyLong(), anyString());

        messageHandler.handle(botEvent);

        verify(userService, times(1)).changeAppUserState(UserStateEnum.ANSWER, botEvent);
        verify(sendMessageForUserFactory, times(1)).createMessageSender();
        verify(sendMessageForUser, times(1)).sendMessage(eq(123L), anyString());
    }

    @Test
    void handleMessage_addWordCommand() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.ADD_WORD.getCommand());
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createMessageSender()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessage(anyLong(), anyString());

        messageHandler.handle(botEvent);

        verify(userService, times(1)).changeAppUserState(UserStateEnum.ADD_MENU, botEvent);
        verify(sendMessageForUserFactory, times(1)).createMessageSender();
        verify(sendMessageForUser, times(1)).sendMessage(eq(123L), anyString());
    }

    @Test
    void handleMessage_learnWordCommand_noWordsToLearn() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.LEARN_WORD.getCommand());
        when(userWordListService.getRandomUserWordList(any(), eq(WordListTypeEnum.LEARNING))).thenReturn(null);
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createMessageSender()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessage(anyLong(), anyString());

        messageHandler.handle(botEvent);

        verify(userService, times(1)).getAppUser(botEvent);
        verify(userWordListService, times(1)).getRandomUserWordList(any(), eq(WordListTypeEnum.LEARNING));
        verify(sendMessageForUserFactory, times(1)).createMessageSender();
        verify(sendMessageForUser, times(1)).sendMessage(eq(123L), anyString());
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
        when(sendMessageForUserFactory.createMessageSender()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessage(anyLong(), anyString());

        messageHandler.handle(botEvent);

        verify(userService, times(1)).getAppUser(botEvent);
        verify(userWordListService, times(1)).getRandomUserWordList(any(), eq(WordListTypeEnum.LEARNING));
        verify(sendMessageForUserFactory, times(1)).createMessageSender();
        verify(sendMessageForUser, times(1)).sendMessage(eq(123L), eq(messageText));
    }
}