package com.example.englingbot.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "questions_for_chat_gpt")
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class QuestionsForChatGpt extends AbstractEntity {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;

    @Column (name = "question")
    private String question;

    @Column (name = "answer")
    private String answer;
}
