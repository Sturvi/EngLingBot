package com.example.englingbot.model;

import com.example.englingbot.model.enums.WordListTypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users_word_lists")
@Data
@NoArgsConstructor
@SuperBuilder
public class UserWordList extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "list_type", columnDefinition = "varchar(255) default 'LEARNING'")
    private WordListTypeEnum listType;

    @Column(name = "timer_value")
    private Integer timerValue;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    private Word word;
}
