package com.example.englingbot.dto.converter;

import com.example.englingbot.dto.WordDto;
import com.example.englingbot.model.Word;
import com.example.englingbot.service.WordService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WordConverter {

    public static Word toEntity(WordDto wordDto) {
        String russianWord = WordService.capitalizeFirstLetter(wordDto.getRussianWord());
        String englishWord = WordService.capitalizeFirstLetter(wordDto.getEnglishWord());

        return Word.builder()
                .englishWord(englishWord)
                .russianWord(russianWord)
                .transcription(wordDto.getTranscription())
                .context(wordDto.getContext())
                .usageExamples(wordDto.getUsageExamples())
                .build();
    }
}
