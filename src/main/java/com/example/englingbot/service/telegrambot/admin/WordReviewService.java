package com.example.englingbot.service.telegrambot.admin;

import com.example.englingbot.model.Word;
import com.example.englingbot.model.WordReview;
import com.example.englingbot.model.dto.converter.WordReviewConverter;
import com.example.englingbot.repository.WordReviewRepository;
import com.example.englingbot.service.externalapi.openai.ChatGptWordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordReviewService {
    private final WordReviewRepository wordReviewRepository;
    private final ChatGptWordUtils chatGptWordUtils;

    public Optional<WordReview> getWordReview (){
        return wordReviewRepository.findTopBy();
    }

    public Optional<WordReview> getWordReviewById (Long id) {
        return  wordReviewRepository.findById(id);
    }

    public void saveWordReview (WordReview wordReview) {
        wordReviewRepository.save(wordReview);
    }

    public void deleteWordReview (WordReview wordReview) {
        wordReviewRepository.delete(wordReview);
    }

    public Long countOfWordInReview (){
        return wordReviewRepository.count();
    }

    public void resendWordToReview (WordReview wordReview) {
        Word word = wordReview.getWord();

        var newReviewDTO = chatGptWordUtils.reviewWordWithChatGpt(word);

        WordReviewConverter.updateEntityFromDTO(wordReview, newReviewDTO);

        saveWordReview(wordReview);
    }
}
