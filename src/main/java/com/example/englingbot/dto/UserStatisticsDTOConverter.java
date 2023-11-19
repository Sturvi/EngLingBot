package com.example.englingbot.dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserStatisticsDTOConverter {

    private final Gson gson;

    public UserStatisticsDTO convert(Tuple tuple) {
        UserStatisticsDTO userStatisticsDTO = new UserStatisticsDTO();
        userStatisticsDTO.setLearningCount((Long) tuple.get("learning_count"));
        userStatisticsDTO.setLearnedCount((Long) tuple.get("learned_count"));
        userStatisticsDTO.setAvailableWordCount((Long) tuple.get("available_word_count"));

        String repetitionLevelCountsJson = (String) tuple.get("repetition_level_counts");
        Type listType = new TypeToken<List<UserStatisticsDTO.RepetitionLevelCount>>(){}.getType();
        List<UserStatisticsDTO.RepetitionLevelCount> repetitionLevelCounts = gson.fromJson(repetitionLevelCountsJson, listType);
        userStatisticsDTO.setRepetitionLevelCounts(repetitionLevelCounts);

        Collections.sort(userStatisticsDTO.getRepetitionLevelCounts());

        return userStatisticsDTO;
    }


}
