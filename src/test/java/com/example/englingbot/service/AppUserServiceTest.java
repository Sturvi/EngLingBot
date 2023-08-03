package com.example.englingbot.service;

import com.example.englingbot.mapper.UserMapper;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.repository.UserRepository;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AppUserService appUserService;
    @Mock
    private BotEvent botEvent;

    @Mock
    private AppUser appUser;

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

        appUserService.saveOrUpdateAppUser(telegramUser);

        verify(userMapper).mapNewUserToUserEntity(telegramUser);
        verify(userRepository).findByTelegramChatId(telegramUser.getId());
        verify(userRepository).save(appUser);
    }

    @Test
    void testGetAppUserWithBotEvent() {
        AppUser appUser = new AppUser();
        when(userRepository.findByTelegramChatId(botEvent.getId())).thenReturn(Optional.empty());
        when(userMapper.mapNewUserToUserEntity(botEvent.getFrom())).thenReturn(appUser);

        AppUser resultUser = appUserService.getAppUser(botEvent);

        assertEquals(appUser, resultUser);
        verify(userMapper).mapNewUserToUserEntity(botEvent.getFrom());
        verify(userRepository).findByTelegramChatId(botEvent.getId());
    }

    @Test
    void testGetAppUserWithChatId() {
        Long chatId = 1L;
        AppUser appUser = new AppUser();
        when(userRepository.findByTelegramChatId(chatId)).thenReturn(Optional.of(appUser));

        Optional<AppUser> resultUserOpt = appUserService.getAppUser(chatId);

        assertTrue(resultUserOpt.isPresent());
        assertEquals(appUser, resultUserOpt.get());
        verify(userRepository).findByTelegramChatId(chatId);
    }

}
