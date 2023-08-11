package com.example.englingbot.repository;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByRussianWordAndEnglishWord(String russianWord, String englishWord);

    @Query("SELECT w FROM Word w WHERE w.russianWord = :word OR w.englishWord = :word")
    List<Word> findByRussianWordOrEnglishWord(String word);

    @Query(value = """
        SELECT w.* 
        FROM words w 
        LEFT JOIN users_vocabulary uv ON w.id = uv.word_id AND uv.user_id = :userId
        WHERE uv.word_id IS NULL 
        ORDER BY RANDOM() 
        LIMIT 10
    """, nativeQuery = true)
    List<Word> findTenRandomWordsNotInUserVocabulary(@Param("userId") Long userId);

}
