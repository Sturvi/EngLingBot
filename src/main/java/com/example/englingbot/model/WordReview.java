package com.example.englingbot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;

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
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    private Word word;

    @Column(name = "is_verified")
    private Boolean isVerified;

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
