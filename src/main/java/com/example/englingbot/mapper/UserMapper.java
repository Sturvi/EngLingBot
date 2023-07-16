package com.example.englingbot.mapper;

import com.example.englingbot.model.UserEntity;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.model.enums.UserStateEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Class for transforming Telegram users into system user entities.
 */
@Component
public class UserMapper {

    /**
     * Creates a new UserEntity entity, filling it with data from a Telegram user.
     *
     * @param user Telegram user object
     * @return UserEntity user entity
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
     * Updates an existing UserEntity entity with data from a Telegram user.
     *
     * @param user Telegram user object
     * @param userEntity User entity that needs to be updated
     */
    public void updateExistingUserEntityFromTelegramUser(User user, UserEntity userEntity) {
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setUsername(user.getUserName());
        userEntity.setUserStatus(true);
    }

    /**
     * Updates the user's state in the system.
     *
     * @param userEntity User entity that needs to be updated
     * @param userStateEnum New user state
     */
    public void updateUserState(UserEntity userEntity, UserStateEnum userStateEnum) {
        userEntity.setUserState(userStateEnum);
    }

    /**
     * Deactivates a user in the system.
     *
     * @param userEntity User entity that needs to be deactivated
     */
    public void deactivateUser(UserEntity userEntity) {
        userEntity.setUserStatus(false);
    }
}
