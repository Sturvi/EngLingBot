package com.example.englingbot.mapper;

import com.example.englingbot.model.UserEntity;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.model.enums.UserStateEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Класс для преобразования пользователей Telegram в сущности пользователей системы.
 */
@Component
public class UserMapper {

    /**
     * Создает новую сущность UserEntity, заполнив ее данными из пользователя Telegram.
     *
     * @param user объект пользователя Telegram
     * @return сущность пользователя UserEntity
     */
    public UserEntity mapNewUserToUserEntity(User user) {
        return UserEntity.builder()
                .telegramChatId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUserName())
                .userStatus(true)
                .userState(UserStateEnum.MAIN)
                .role(UserRoleEnum.USER)
                .build();
    }

    /**
     * Обновляет существующую сущность UserEntity данными из пользователя Telegram.
     *
     * @param user объект пользователя Telegram
     * @param userEntity сущность пользователя, которую нужно обновить
     */
    public void updateExistingUserEntityFromTelegramUser(User user, UserEntity userEntity) {
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setUsername(user.getUserName());
        userEntity.setUserStatus(true);
    }

    /**
     * Обновляет состояние пользователя в системе.
     *
     * @param userEntity сущность пользователя, которую нужно обновить
     * @param userStateEnum новое состояние пользователя
     */
    public void updateUserState(UserEntity userEntity, UserStateEnum userStateEnum) {
        userEntity.setUserState(userStateEnum);
    }

    /**
     * Деактивирует пользователя в системе.
     *
     * @param userEntity сущность пользователя, которую нужно деактивировать
     */
    public void deactivateUser(UserEntity userEntity) {
        userEntity.setUserStatus(false);
    }
}
