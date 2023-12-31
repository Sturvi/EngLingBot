package com.example.englingbot.model;

import com.example.englingbot.model.enums.UserWordState;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users_vocabulary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "word_id"}))
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserVocabulary extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "list_type", columnDefinition = "varchar(255) default 'LEARNING'")
    private UserWordState listType;

    @Column(name = "timer_value", columnDefinition = "0")
    private Integer timerValue;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    private Word word;

    @Column(name = "last_retry", nullable = false)
    private LocalDateTime lastRetry;

    @Column(name = "failed_attempts", nullable = false)
    private Integer failedAttempts;
}
