package com.example.englingbot.repository;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByAppUserAndIsActive(AppUser appUser, boolean isActive);

    Optional<Chat> findFirstByAppUserAndIsActive (AppUser appUser, boolean isActive);
}
