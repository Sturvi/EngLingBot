package com.example.englingbot.repository;

import com.example.englingbot.model.UserWordList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWordListRepository extends JpaRepository<UserWordList, Long> {
}
