package com.example.englingbot.service;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Chat;
import com.example.englingbot.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRepository chatRepository;


    public void cleanChatHistory (AppUser appUser) {
        Optional<Chat> chatOpt = chatRepository.findFirstByAppUserAndIsActive(appUser, true);

        chatOpt.ifPresent(chat -> {
            chat.setActive(false);
            save(chat);
        });
    }

    public Optional<Chat> getActiveChat (AppUser appUser) {
        return chatRepository.findFirstByAppUserAndIsActive(appUser, true);
    }

    public void save (Chat chat) {
        chatRepository.save(chat);
    }
}
