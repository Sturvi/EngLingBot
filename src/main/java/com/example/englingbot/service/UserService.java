package com.example.englingbot.service;

import com.example.englingbot.BotEvent;
import com.example.englingbot.mapper.UserMapper;
import com.example.englingbot.model.UserEntity;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Сервис для управления пользователями.
 * Предоставляет методы для сохранения, обновления и получения пользователей.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Сохраняет или обновляет информацию о пользователе.
     * @param user пользователь для сохранения или обновления.
     * @return сохраненный или обновленный пользователь.
     */
    public UserEntity saveOrUpdateUser(User user) {
        log.debug("Обновление информации о пользователе с ID: {}", user.getId());

        return userRepository.findByTelegramChatId(user.getId()).map(existingUserEntity -> {
            log.debug("Пользователь с ID: {} найден в базе данных, обновление...", user.getId());
            updateUserInDataBase(user, existingUserEntity);
            return userRepository.save(existingUserEntity);
        }).orElseGet(() -> {
            log.debug("Пользователь с ID: {} не найден в базе данных, создание нового пользователя...", user.getId());
            return saveUserInDataBase(user);
        });
    }

    /**
     * Сохраняет нового пользователя в базу данных.
     * @param user пользователь для сохранения.
     * @return сохраненный пользователь.
     */
    private UserEntity saveUserInDataBase(User user) {
        log.debug("Сохранение пользователя с ID: {}", user.getId());

        UserEntity userEntity = userMapper.mapNewUserToUserEntity(user);

        userRepository.save(userEntity);

        return userEntity;
    }

    /**
     * Обновляет существующего пользователя в базе данных.
     * @param user пользователь для обновления.
     * @param userEntity сущность пользователя для обновления.
     * @return обновленный пользователь.
     */
    private UserEntity updateUserInDataBase(User user, UserEntity userEntity){
        userMapper.updateExistingUserEntityFromTelegramUser(user, userEntity);
        return userEntity;
    }

    /**
     * Изменяет состояние пользователя.
     * @param newUserState новое состояние пользователя.
     * @param botEvent событие бота.
     */
    public void changeUserState(UserStateEnum newUserState, BotEvent botEvent) {
        log.debug("Обновление состояния пользователя для чата с ID: {}", botEvent.getId());

        UserEntity userEntity = userRepository
                .findByTelegramChatId(botEvent.getId())
                .orElseGet(() -> userMapper.mapNewUserToUserEntity(botEvent.getFrom()));

        userMapper.updateUserState(userEntity, newUserState);

        userRepository.save(userEntity);
        log.debug("Состояние пользователя для чата с ID: {} успешно обновлено на {}", botEvent.getId(), newUserState);
    }

    /**
     * Получает состояние пользователя.
     * @param botEvent событие бота.
     * @return состояние пользователя.
     */
    public UserStateEnum getUserState(BotEvent botEvent) {
        UserEntity userEntity = getUserEntityFromDataBase(botEvent);

        return userEntity.getUserState();
    }

    /**
     * Получает состояние пользователя по ID чата.
     * @param chatId ID чата.
     * @return состояние пользователя.
     */
    public UserStateEnum getUserState(Long chatId) {
        var userEntity = getUserEntityFromDataBase(chatId);

        if (userEntity.isPresent()) {
            return userEntity.get().getUserState();
        } else {
            return UserStateEnum.MAIN;
        }
    }

    /**
     * Получает роль пользователя.
     * @param botEvent событие бота.
     * @return роль пользователя.
     */
    public UserRoleEnum getUserRole (BotEvent botEvent) {
        UserEntity userEntity = getUserEntityFromDataBase(botEvent);

        return userEntity.getRole();
    }

    /**
     * Получает сущность пользователя из базы данных.
     * @param botEvent событие бота.
     * @return сущность пользователя.
     */
    public UserEntity getUserEntityFromDataBase (BotEvent botEvent) {
        return userRepository
                .findByTelegramChatId(botEvent.getId())
                .orElseGet(() -> userMapper.mapNewUserToUserEntity(botEvent.getFrom()));
    }

    /**
     * Получает сущность пользователя из базы данных по ID чата.
     * @param chatId ID чата.
     * @return сущность пользователя.
     */
    public Optional<UserEntity> getUserEntityFromDataBase (Long chatId) {
        return userRepository
                .findByTelegramChatId(chatId);
    }

    /**
     * Деактивирует пользователя.
     * @param botEvent событие бота.
     */
    public void deactivateUser (BotEvent botEvent) {
        var userEntityOpt = userRepository.findByTelegramChatId(botEvent.getId());

        if (userEntityOpt.isPresent()) {
            UserEntity user = userEntityOpt.get();
            userMapper.deactivateUser(user);
            userRepository.save(user);
            log.debug("Пользователь с чатом ID: {} деактивирован", botEvent.getId());
        }
    }
}
