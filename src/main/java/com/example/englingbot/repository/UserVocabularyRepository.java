package com.example.englingbot.repository;

import com.example.englingbot.dto.UserStatisticsDTO;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;
import jakarta.persistence.Tuple;
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

    List<UserVocabulary> findByUserAndWordIn(AppUser user, List<Word> words);

    void deleteByUserAndWord(AppUser user, Word word);

    Optional<UserVocabulary> findByUserAndWordId(AppUser user, Long wordId);

    @Query("SELECT uv.user FROM UserVocabulary uv WHERE uv.word = :word")
    List<AppUser> findUsersByWord(@Param("word") Word word);

    @Query(value = "WITH stats AS (" +
            "    SELECT " +
            "        COUNT(*) FILTER (WHERE uv.list_type = 'LEARNING') AS learning_count, " +
            "        COUNT(*) FILTER (WHERE uv.list_type = 'LEARNED') AS learned_count, " +
            "        COUNT(*) FILTER (WHERE uv.list_type = 'LEARNING' AND (uv.last_retry + CAST(uv.timer_value || ' days' AS INTERVAL)) < CURRENT_TIMESTAMP) AS available_word_count " +
            "    FROM " +
            "        users_vocabulary uv " +
            "    WHERE " +
            "        uv.user_id = :userId " +
            "), rep_counts AS (" +
            "    SELECT " +
            "        uv.timer_value, " +
            "        COUNT(*) AS count " +
            "    FROM " +
            "        users_vocabulary uv " +
            "    WHERE " +
            "        uv.user_id = :userId AND uv.list_type = 'LEARNING' " +
            "    GROUP BY " +
            "        uv.timer_value" +
            ") " +
            "SELECT " +
            "    s.learning_count, " +
            "    s.learned_count, " +
            "    s.available_word_count, " +
            "    json_agg(json_build_object('level', rc.timer_value, 'count', rc.count)) AS repetition_level_counts " +
            "FROM " +
            "    stats s " +
            "CROSS JOIN " +
            "    rep_counts rc " +
            "GROUP BY " +
            "    s.learning_count, " +
            "    s.learned_count, " +
            "    s.available_word_count;", nativeQuery = true)
    Tuple getUserStatisticsTuple(@Param("userId") Long userId);

}
