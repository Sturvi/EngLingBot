package com.example.englingbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "words", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"russian_word", "english_word"}, name = "unique_word_pair_idx")
})
@NoArgsConstructor
@SuperBuilder
@Data
public class Word extends TimestampWithId{

    @Column(name = "russian_word", nullable = false)
    private String russianWord;

    @Column(name = "english_word", nullable = false)
    private String englishWord;

    @Column(name = "transcription")
    private String transcription;

    @Column(name = "context")
    private String context;

    @Column(name = "usage_examples")
    private String usageExamples;
}
