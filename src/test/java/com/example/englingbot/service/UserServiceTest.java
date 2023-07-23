package com.example.englingbot.service;

import com.example.englingbot.mapper.UserMapper;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.repository.UserRepository;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;
    @Mock
    private BotEvent botEvent;

    @BeforeEach
    void init() {
        lenient().when(botEvent.getId()).thenReturn(1L);
    }

    @Test
    void testSaveOrUpdateAppUser() {
        User telegramUser = mock(User.class);
        when(telegramUser.getId()).thenReturn(1L);
        AppUser appUser = new AppUser();
        when(userRepository.findByTelegramChatId(telegramUser.getId())).thenReturn(Optional.empty());
        when(userMapper.mapNewUserToUserEntity(telegramUser)).thenReturn(appUser);

        userService.saveOrUpdateAppUser(telegramUser);

        verify(userMapper).mapNewUserToUserEntity(telegramUser);
        verify(userRepository).findByTelegramChatId(telegramUser.getId());
        verify(userRepository).save(appUser);
    }

    @Test
    void testChangeAppUserState() {
        AppUser appUser = new AppUser();
        UserStateEnum newUserState = UserStateEnum.MAIN;
        when(userRepository.findByTelegramChatId(botEvent.getId())).thenReturn(Optional.empty());
        when(userMapper.mapNewUserToUserEntity(botEvent.getFrom())).thenReturn(appUser);

        userService.changeAppUserState(newUserState, botEvent);

        verify(userMapper).updateUserState(appUser, newUserState);

        verify(userRepository).save(appUser);
    }

    @Test
    void testDeactivateAppUser() {
        BotEvent botEvent = Mockito.mock(BotEvent.class);
        when(botEvent.getId()).thenReturn(1L);
        AppUser appUser = new AppUser();
        when(userRepository.findByTelegramChatId(botEvent.getId())).thenReturn(Optional.of(appUser));

        userService.deactivateAppUser(botEvent);

        verify(userMapper).deactivateUser(appUser);
        verify(userRepository).save(appUser);
    }

    @Test
    void testGetAppUserStateWithBotEvent() {
        AppUser appUser = new AppUser();
        appUser.setUserState(UserStateEnum.MAIN);
        when(userRepository.findByTelegramChatId(botEvent.getId())).thenReturn(Optional.empty());
        when(userMapper.mapNewUserToUserEntity(botEvent.getFrom())).thenReturn(appUser);

        UserStateEnum userStateEnum = userService.getAppUserState(botEvent);

        assertEquals(UserStateEnum.MAIN, userStateEnum);
        verify(userMapper).mapNewUserToUserEntity(botEvent.getFrom());
        verify(userRepository).findByTelegramChatId(botEvent.getId());
    }

    @Test
    void testGetAppUserStateWithChatId() {
        Long chatId = 1L;
        AppUser appUser = new AppUser();
        appUser.setUserState(UserStateEnum.MAIN);
        when(userRepository.findByTelegramChatId(chatId)).thenReturn(Optional.of(appUser));

        UserStateEnum userStateEnum = userService.getAppUserState(chatId);

        assertEquals(UserStateEnum.MAIN, userStateEnum);
        verify(userRepository).findByTelegramChatId(chatId);
    }

    @Test
    void testGetAppUserRole() {
        AppUser appUser = new AppUser();
        appUser.setRole(UserRoleEnum.USER);
        when(userRepository.findByTelegramChatId(botEvent.getId())).thenReturn(Optional.empty());
        when(userMapper.mapNewUserToUserEntity(botEvent.getFrom())).thenReturn(appUser);

        UserRoleEnum userRoleEnum = userService.getAppUserRole(botEvent);

        assertEquals(UserRoleEnum.USER, userRoleEnum);
        verify(userMapper).mapNewUserToUserEntity(botEvent.getFrom());
        verify(userRepository).findByTelegramChatId(botEvent.getId());
    }

    @Test
    void testGetAppUserWithBotEvent() {
        AppUser appUser = new AppUser();
        when(userRepository.findByTelegramChatId(botEvent.getId())).thenReturn(Optional.empty());
        when(userMapper.mapNewUserToUserEntity(botEvent.getFrom())).thenReturn(appUser);

        AppUser resultUser = userService.getAppUser(botEvent);

        assertEquals(appUser, resultUser);
        verify(userMapper).mapNewUserToUserEntity(botEvent.getFrom());
        verify(userRepository).findByTelegramChatId(botEvent.getId());
    }

    @Test
    void testGetAppUserWithChatId() {
        Long chatId = 1L;
        AppUser appUser = new AppUser();
        when(userRepository.findByTelegramChatId(chatId)).thenReturn(Optional.of(appUser));

        Optional<AppUser> resultUserOpt = userService.getAppUser(chatId);

        assertTrue(resultUserOpt.isPresent());
        assertEquals(appUser, resultUserOpt.get());
        verify(userRepository).findByTelegramChatId(chatId);
    }

}
