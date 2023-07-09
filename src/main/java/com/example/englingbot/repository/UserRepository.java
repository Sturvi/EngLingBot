package com.example.englingbot.repository;

import com.example.englingbot.model.UserEntity;
import com.example.englingbot.model.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByTelegramChatId(Long telegramChatId);

    List<UserEntity> findAllByRole(UserRoleEnum role);
}
