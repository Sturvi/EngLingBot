package com.example.englingbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "questions_for_chatGpt")
@Data
@NoArgsConstructor
@SuperBuilder
public class QuestionsForChatGpt extends TimestampWithId {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column (name = "question")
    private String question;

    @Column (name = "answer")
    private String answer;
}
