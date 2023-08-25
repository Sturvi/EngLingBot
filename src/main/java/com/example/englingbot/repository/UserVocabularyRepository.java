package com.example.englingbot.repository;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long> {

    @Query("SELECT uwl FROM UserVocabulary uwl WHERE uwl.user = :user AND uwl.listType IN :types")
    List<UserVocabulary> findByUserAndListTypeIn(@Param("user") AppUser user, @Param("types") List<UserWordState> types);

    UserVocabulary findByUserAndWord(AppUser user, Word word);

    Long countByUserAndListType(AppUser user, UserWordState listType);

    Long countByUserAndListTypeAndTimerValue(AppUser user, UserWordState listType, Integer timerValue);

    @Query("SELECT MAX(uv.timerValue) FROM UserVocabulary uv WHERE uv.user = :user AND uv.listType = :listType")
    Optional<Integer> findTopTimerValueByUserAndListType(AppUser user, UserWordState listType);

    List<UserVocabulary> findByUserAndWordIn(AppUser user, List<Word> words);

    void deleteByUserAndWord(AppUser user, Word word);

    @Query("SELECT uwl FROM UserVocabulary uwl WHERE uwl.listType IN :types")
    List<UserVocabulary> findByListTypeIn(@Param("types") List<UserWordState> types);


    @Query("SELECT uv.user FROM UserVocabulary uv WHERE uv.word = :word")
    List<AppUser> findUsersByWord(@Param("word") Word word);
}
