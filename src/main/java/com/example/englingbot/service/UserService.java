package com.example.englingbot.service;

import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.mapper.UserMapper;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;


/**
 * The UserService class provides methods for managing users.
 * It provides methods for saving, updating, and retrieving users.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Saves or updates the user information.
     *
     * @param user The user to save or update.
     * @return The saved or updated user.
     */
    public AppUser saveOrUpdateAppUser(User user) {
        log.debug("Updating user information for ID: {}", user.getId());

        return userRepository.findByTelegramChatId(user.getId()).map(existingUserEntity -> {
            log.debug("User with ID: {} found in the database, updating...", user.getId());
            updateAppUserInDataBase(user, existingUserEntity);
            return userRepository.save(existingUserEntity);
        }).orElseGet(() -> {
            log.debug("User with ID: {} not found in the database, creating a new user...", user.getId());
            return saveAppUser(user);
        });
    }

    /**
     * Saves a new user in the database.
     *
     * @param user The user to save.
     * @return The saved user.
     */
    private AppUser saveAppUser(User user) {
        log.debug("Saving user with ID: {}", user.getId());

        AppUser userEntity = userMapper.mapNewUserToUserEntity(user);

        userRepository.save(userEntity);

        return userEntity;
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user        The user to update.
     * @param userEntity  The user entity to update.
     * @return The updated user.
     */
    private AppUser updateAppUserInDataBase(User user, AppUser userEntity) {
        userMapper.updateExistingUserEntityFromTelegramUser(user, userEntity);
        return userEntity;
    }

    /**
     * Changes the user state.
     *
     * @param newUserState The new user state.
     * @param botEvent     The bot event.
     */
    public void changeAppUserState(UserStateEnum newUserState, BotEvent botEvent) {
        log.debug("Updating user state for chat ID: {}", botEvent.getId());

        AppUser userEntity = userRepository
                .findByTelegramChatId(botEvent.getId())
                .orElseGet(() -> userMapper.mapNewUserToUserEntity(botEvent.getFrom()));

        userMapper.updateUserState(userEntity, newUserState);

        userRepository.save(userEntity);
        log.debug("User state for chat ID: {} successfully updated to {}", botEvent.getId(), newUserState);
    }

    /**
     * Gets the user state.
     *
     * @param botEvent The bot event.
     * @return The user state.
     */
    public UserStateEnum getAppUserState(BotEvent botEvent) {
        AppUser userEntity = getAppUser(botEvent);

        return userEntity.getUserState();
    }

    /**
     * Gets the user state by chat ID.
     *
     * @param chatId The chat ID.
     * @return The user state.
     */
    public UserStateEnum getAppUserState(Long chatId) {
        var userEntity = getAppUser(chatId);

        if (userEntity.isPresent()) {
            return userEntity.get().getUserState();
        } else {
            return UserStateEnum.MAIN;
        }
    }

    /**
     * Gets the user role.
     *
     * @param botEvent The bot event.
     * @return The user role.
     */
    public UserRoleEnum getAppUserRole(BotEvent botEvent) {
        AppUser userEntity = getAppUser(botEvent);

        return userEntity.getRole();
    }

    /**
     * Gets the user entity from the database.
     *
     * @param botEvent The bot event.
     * @return The user entity.
     */
    public AppUser getAppUser(BotEvent botEvent) {
        return userRepository
                .findByTelegramChatId(botEvent.getId())
                .orElseGet(() -> userMapper.mapNewUserToUserEntity(botEvent.getFrom()));
    }

    /**
     * Gets the user entity from the database by chat ID.
     *
     * @param chatId The chat ID.
     * @return The user entity.
     */
    public Optional<AppUser> getAppUser(Long chatId) {
        return userRepository
                .findByTelegramChatId(chatId);
    }

    /**
     * Deactivates a user.
     *
     * @param botEvent The bot event.
     */
    public void deactivateAppUser(BotEvent botEvent) {
        var userEntityOpt = userRepository.findByTelegramChatId(botEvent.getId());

        if (userEntityOpt.isPresent()) {
            AppUser user = userEntityOpt.get();
            userMapper.deactivateUser(user);
            userRepository.save(user);
            log.debug("User with chat ID: {} deactivated", botEvent.getId());
        }
    }
}
