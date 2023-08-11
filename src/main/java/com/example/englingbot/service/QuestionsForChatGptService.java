package com.example.englingbot.service;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.QuestionsForChatGpt;
import com.example.englingbot.repository.QuestionsForChatGptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionsForChatGptService {
    private final QuestionsForChatGptRepository questionsForChatGptRepository;

    public void addNewQuestions (AppUser appUser, String questions, String answer){
        QuestionsForChatGpt questionsForChatGpt = QuestionsForChatGpt.builder()
                .user(appUser)
                .question(questions)
                .answer(answer)
                .build();

        questionsForChatGptRepository.save(questionsForChatGpt);
    }
}
