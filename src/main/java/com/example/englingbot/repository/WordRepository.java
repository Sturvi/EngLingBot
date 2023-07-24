package com.example.englingbot.repository;

import com.example.englingbot.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByRussianWordAndEnglishWord(String russianWord, String englishWord);
}
