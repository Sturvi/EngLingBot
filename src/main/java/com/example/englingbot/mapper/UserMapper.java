package com.example.englingbot.mapper;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.model.enums.UserStateEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Class for transforming Telegram users into system user entities.
 */
public class UserMapper {

    /**
     * Creates a new UserEntity entity, filling it with data from a Telegram user.
     *
     * @param user Telegram user object
     * @return UserEntity user entity
     */
    public static AppUser mapNewUserToUserEntity(User user) {
        return AppUser.builder()
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
     * Updates an existing UserEntity entity with data from a Telegram user.
     *
     * @param user Telegram user object
     * @param userEntity User entity that needs to be updated
     */
    public static void updateExistingUserEntityFromTelegramUser(User user, AppUser userEntity) {
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setUsername(user.getUserName());
        userEntity.setUserStatus(true);
    }

    /**
     * Deactivates a user in the system.
     *
     * @param userEntity User entity that needs to be deactivated
     */
    public static void deactivateUser(AppUser userEntity) {
        userEntity.setUserStatus(false);
    }
}
