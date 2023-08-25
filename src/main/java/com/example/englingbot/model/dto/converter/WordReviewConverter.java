package com.example.englingbot.model.dto.converter;

import com.example.englingbot.model.WordReview;
import com.example.englingbot.model.dto.WordReviewDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WordReviewConverter {

    public static void updateEntityFromDTO(WordReview entity, WordReviewDTO dto) {
        if (entity.getWord().equals(dto.getWord())) {
            entity.setChatGptResponse(dto.getChatGptResponse());
            entity.setChatGptResponseText(dto.getChatGptResponseText());
        } else {
            log.warn("Words in the entity and DTO are not the same. No data was copied.");
        }
    }

    public static WordReview toEntity (WordReviewDTO wordReviewDTO){
        return WordReview.builder()
                .word(wordReviewDTO.getWord())
                .chatGptResponse(wordReviewDTO.getChatGptResponse())
                .chatGptResponseText(wordReviewDTO.getChatGptResponseText())
                .build();
    }
}
