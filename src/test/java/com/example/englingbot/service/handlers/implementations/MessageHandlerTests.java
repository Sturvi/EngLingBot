package com.example.englingbot.service.handlers.implementations;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.service.UserVocabularyService;
import com.example.englingbot.service.comandsenums.TextCommandsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.message.sendtextmessage.SendMessageForUser;
import com.example.englingbot.service.message.sendtextmessage.SendMessageForUserFactory;
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
    private UserVocabularyService userVocabularyService;
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
        when(userVocabularyService.getRandomUserVocabulary(any(), eq(UserWordState.LEARNING))).thenReturn(null);
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createNewMessage()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessageWithReplyKeyboard(anyLong(), anyString());

        messageHandler.handle(botEvent, appUser);

        verify(userVocabularyService, times(1)).getRandomUserVocabulary(any(), eq(UserWordState.LEARNING));
        verify(sendMessageForUserFactory, times(1)).createNewMessage();
        verify(sendMessageForUser, times(1)).sendMessageWithReplyKeyboard(eq(123L), anyString());
    }

    @Test
    void handleMessage_learnWordCommand_hasWordsToLearn() {
        BotEvent botEvent = mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(123L);
        when(botEvent.getText()).thenReturn(TextCommandsEnum.LEARN_WORD.getCommand());
        var wordFromUserDictionary = mock(UserVocabulary.class);
        when(userVocabularyService.getRandomUserVocabulary(any(), eq(UserWordState.LEARNING))).thenReturn(wordFromUserDictionary);
        String messageText = "A word to send to the user.";
        when(userVocabularyService.getWordWithStatus(wordFromUserDictionary)).thenReturn(messageText);
        var sendMessageForUser = Mockito.mock(SendMessageForUser.class);
        when(sendMessageForUserFactory.createNewMessage()).thenReturn(sendMessageForUser);
        doNothing().when(sendMessageForUser).sendMessageWithReplyKeyboard(anyLong(), anyString());

        messageHandler.handle(botEvent, appUser);

        verify(userVocabularyService, times(1)).getRandomUserVocabulary(any(), eq(UserWordState.LEARNING));
        verify(sendMessageForUserFactory, times(1)).createNewMessage();
        verify(sendMessageForUser, times(1)).sendMessageWithReplyKeyboard(eq(123L), eq(messageText));
    }
}