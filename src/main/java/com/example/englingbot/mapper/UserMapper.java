package com.example.englingbot.mapper;

import com.example.englingbot.model.UserEntity;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.model.enums.UserStateEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class UserMapper {

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

    public void updateExistingUserEntityFromTelegramUser(User user, UserEntity userEntity) {
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setUsername(user.getUserName());
        userEntity.setUserStatus(true);
    }

    public void updateUserState(UserEntity userEntity, UserStateEnum userStateEnum) {
        userEntity.setUserState(userStateEnum);
    }

}
