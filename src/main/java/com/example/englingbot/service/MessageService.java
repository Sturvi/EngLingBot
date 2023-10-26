package com.example.englingbot.service;

import com.example.englingbot.model.Message;
import com.example.englingbot.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;


    public void save (Message message) {
        messageRepository.save(message);
    }
}
