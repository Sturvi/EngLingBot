package com.example.englingbot.model.dto;

import com.example.englingbot.model.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordReviewDTO {
    private Word word;
    private Boolean chatGptResponse;
    private String chatGptResponseText;
}
