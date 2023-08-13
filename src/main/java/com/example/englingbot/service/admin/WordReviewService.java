package com.example.englingbot.service.admin;

import com.example.englingbot.model.WordReview;
import com.example.englingbot.repository.WordReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordReviewService {
    private final WordReviewRepository wordReviewRepository;

    public Optional<WordReview> getWordReview (){
        return wordReviewRepository.findTopByIsVerifiedFalse();
    }
}
