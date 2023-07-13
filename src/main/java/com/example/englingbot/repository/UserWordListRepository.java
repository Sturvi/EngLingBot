package com.example.englingbot.repository;

import com.example.englingbot.model.UserEntity;
import com.example.englingbot.model.UserWordList;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.WordListTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserWordListRepository extends JpaRepository<UserWordList, Long> {

    @Query("SELECT uwl FROM UserWordList uwl WHERE uwl.user = :user AND uwl.listType IN :types")
    List<UserWordList> findByUserAndListTypeIn(@Param("user") UserEntity user, @Param("types") List<WordListTypeEnum> types);

}
