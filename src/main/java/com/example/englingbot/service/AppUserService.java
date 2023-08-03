package com.example.englingbot.service;

import com.example.englingbot.mapper.UserMapper;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.repository.UserRepository;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
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
public class AppUserService {

    private final UserRepository userRepository;

    /**
     * Saves or updates the user information.
     *
     * @param user The user to save or update.
     * @return The saved or updated user.
     */
    public AppUser saveOrUpdateAppUser(User user) {
        log.debug("Starting saveOrUpdateAppUser method for ID: {}", user.getId());

        AppUser appUser = userRepository.findByTelegramChatId(user.getId()).map(existingUserEntity -> {
            log.debug("User with ID: {} found in the database, updating...", user.getId());
            updateAppUserInDataBase(user, existingUserEntity);
            return userRepository.save(existingUserEntity);
        }).orElseGet(() -> {
            log.debug("User with ID: {} not found in the database, creating a new user...", user.getId());
            return saveAppUser(user);
        });

        log.debug("Ending saveOrUpdateAppUser method for ID: {}", user.getId());
        return appUser;
    }

    /**
     * Saves a new user in the database.
     *
     * @param user The user to save.
     * @return The saved user.
     */
    private AppUser saveAppUser(User user) {
        log.debug("Saving user with ID: {}", user.getId());

        AppUser userEntity = UserMapper.mapNewUserToUserEntity(user);

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
        UserMapper.updateExistingUserEntityFromTelegramUser(user, userEntity);
        return userEntity;
    }

    /**
     * Gets the user entity from the database.
     *
     * @param botEvent The bot event.
     * @return The user entity.
     */
    public AppUser getAppUser(BotEvent botEvent) {
        log.debug("Getting user with ID: {}", botEvent.getId());
        return userRepository
                .findByTelegramChatId(botEvent.getId())
                .orElseGet(() -> UserMapper.mapNewUserToUserEntity(botEvent.getFrom()));
    }

    /**
     * Gets the user entity from the database by chat ID.
     *
     * @param chatId The chat ID.
     * @return The user entity.
     */
    public Optional<AppUser> getAppUser(Long chatId) {
        log.debug("Getting user with chat ID: {}", chatId);
        return userRepository
                .findByTelegramChatId(chatId);
    }

    /**
     * Saves the provided user in the database.
     *
     * @param appUser The user to save.
     * @return The saved user.
     */
    public AppUser save(AppUser appUser) {
        log.debug("Saving user entity...");
        userRepository.save(appUser);
        return appUser;
    }
}
