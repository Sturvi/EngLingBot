package com.example.englingbot.model;

import com.example.englingbot.model.enums.UserWordState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users_vocabulary")
@Data
@NoArgsConstructor
@SuperBuilder
public class UserVocabulary extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "list_type", columnDefinition = "varchar(255) default 'LEARNING'")
    private UserWordState listType;

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
