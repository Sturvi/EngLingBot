package com.example.englingbot.repository;

import com.example.englingbot.model.QuestionsForChatGpt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionsForChatGptRepository extends JpaRepository<QuestionsForChatGpt, Long> {

}
