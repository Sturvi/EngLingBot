package com.example.englingbot.repository;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramChatId(Long telegramChatId);

    List<AppUser> findAllByRole(UserRoleEnum role);
}
