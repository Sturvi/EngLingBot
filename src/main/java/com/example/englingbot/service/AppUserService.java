package com.example.englingbot.service;

import com.example.englingbot.mapper.UserMapper;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.repository.AppUserRepository;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;


/**
 * The UserService class provides methods for managing users.
 * It provides methods for saving, updating, and retrieving users.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final UserMapper userMapper;

    /**
     * Saves or updates the user information.
     *
     * @param user The user to save or update.
     * @return The saved or updated user.
     */
    public AppUser saveOrUpdateAppUser(User user) {
        log.trace("Saving or updating AppUser");

        AppUser appUser = appUserRepository.findByTelegramChatId(user.getId()).map(existingUserEntity -> {
            log.debug("Updating existing AppUser in the database");
            updateAppUserInDataBase(user, existingUserEntity);
            return appUserRepository.save(existingUserEntity);
        }).orElseGet(() -> {
            log.debug("Saving new AppUser to the database");
            return saveAppUser(user);
        });

        log.trace("AppUser saved or updated successfully");

        return appUser;
    }

    /**
     * Saves a new user in the database.
     *
     * @param user The user to save.
     * @return The saved user.
     */
    private AppUser saveAppUser(User user) {
        log.trace("Saving AppUser");
        AppUser appUser = appUserRepository.saveAndFlush(userMapper.mapNewUserToUserEntity(user));
        log.debug("AppUser saved successfully");
        return appUser;
    }


    /**
     * Updates an existing user in the database.
     *
     * @param user       The user to update.
     * @param userEntity The user entity to update.
     */
    private void updateAppUserInDataBase(User user, AppUser userEntity) {
        log.trace("Updating AppUser in the database");
        log.debug("Updating user entity from Telegram user: {}", user);
        userMapper.updateExistingUserEntityFromTelegramUser(user, userEntity);
        log.debug("Updated user entity: {}", userEntity);
        log.trace("AppUser updated successfully");
    }

    /**
     * Gets the user entity from the database.
     *
     * @param botEvent The bot event.
     * @return The user entity.
     */
    public AppUser getAppUser(BotEvent botEvent) {
        log.trace("Entering getAppUser method");
        AppUser appUser = appUserRepository
                .findByTelegramChatId(botEvent.getId())
                .orElseGet(() -> {
                    log.debug("Telegram chat ID not found in the database. Creating a new user.");
                    return userMapper.mapNewUserToUserEntity(botEvent.getFrom());
                });
        log.trace("Exiting getAppUser method");
        return appUser;
    }

    /**
     * Gets the user entity from the database by chat ID.
     *
     * @param chatId The chat ID.
     * @return The user entity.
     */
    public Optional<AppUser> getAppUser(Long chatId) {
        log.trace("Entering getAppUser method");
        log.debug("Getting user with chat ID: {}", chatId);
        Optional<AppUser> user = appUserRepository.findByTelegramChatId(chatId);
        log.trace("Exiting getAppUser method");
        return user;

    }

    /**
     * Saves the provided user in the database.
     *
     * @param appUser The user to save.
     * @return The saved user.
     */
    public AppUser save(AppUser appUser) {
        log.trace("Entering save method");
        log.debug("Saving user entity...");
        appUserRepository.save(appUser);
        log.trace("Exiting save method");
        return appUser;
    }

    public List<AppUser> getAppUserListByRole (UserRoleEnum userRoleEnum){
        return appUserRepository.findAllByRole(userRoleEnum);
    }
}
