package com.example.englingbot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "questions_for_chatGpt")
@Setter
@Getter

// TODO джава по-умолчанию генерирует пустой конструктор, если в классе не определены другие конструкторы
//  NoArgsConstructor здесь не нужна
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
