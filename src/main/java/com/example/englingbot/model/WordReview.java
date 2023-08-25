package com.example.englingbot.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "word_review")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class WordReview extends AbstractEntity {

    @OneToOne
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    private Word word;

    @Column(name = "chat_gpt_response")
    private Boolean chatGptResponse;

    @Column(name = "chat_gpt_response_text", columnDefinition = "TEXT")
    private String chatGptResponseText;

    @Override
    public String toString() {
        return word +
                "\n\nРезюме от chat gpt:\n" + chatGptResponse +
                "\n\nОбоснование:\n" + chatGptResponseText ;
    }
}
