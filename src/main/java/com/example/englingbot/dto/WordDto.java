package com.example.englingbot.dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordDto {
    private String russianWord;
    private String englishWord;
    private String transcription;
    private String context;
    private String usageExamples;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordDto wordDto = (WordDto) o;
        return Objects.equals(russianWord, wordDto.russianWord) && Objects.equals(englishWord, wordDto.englishWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(russianWord, englishWord);
    }
}
