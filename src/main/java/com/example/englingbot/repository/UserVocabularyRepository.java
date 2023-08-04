package com.example.englingbot.repository;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.enums.UserWordState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long> {

    @Query("SELECT uwl FROM UserVocabulary uwl WHERE uwl.user = :user AND uwl.listType IN :types")
    List<UserVocabulary> findByUserAndListTypeIn(@Param("user") AppUser user, @Param("types") List<UserWordState> types);

}
