package com.example.englingbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "words", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"russian_word", "english_word"}, name = "unique_word_pair_idx")
})
@NoArgsConstructor
@SuperBuilder
@Data
public class Word extends AbstractEntity {

    @OneToOne(mappedBy = "word", cascade = CascadeType.REMOVE)
    private WordReview wordReview;

    @OneToMany(mappedBy = "word", cascade = CascadeType.REMOVE)
    private List<UserVocabulary> userVocabularies;

    @Column(name = "russian_word", nullable = false)
    private String russianWord;

    @Column(name = "english_word", nullable = false)
    private String englishWord;

    @Column(name = "transcription")
    private String transcription;

    @Column(name = "context", columnDefinition = "TEXT")
    private String context;

    @Column(name = "usage_examples", columnDefinition = "TEXT")
    private String usageExamples;

    public void setRussianWord(String russianWord) {
        if (russianWord != null && !russianWord.isEmpty()) {
            this.russianWord = russianWord.substring(0, 1).toUpperCase() + russianWord.substring(1);
        }
    }

    public void setEnglishWord(String englishWord) {
        if (englishWord != null && !englishWord.isEmpty()) {
            this.englishWord = englishWord.substring(0, 1).toUpperCase() + englishWord.substring(1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(russianWord, word.russianWord) && Objects.equals(englishWord, word.englishWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), russianWord, englishWord);
    }

    @Override
    public String toString() {
        return  englishWord + "  -  " + russianWord;
    }
}
