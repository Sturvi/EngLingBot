package com.example.englingbot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserStatisticsDTO {
    private Long learningCount;
    private Long learnedCount;
    private Long repetitionCount;
    private Long availableWordCount;
    private List<RepetitionLevelCount> repetitionLevelCounts;


    @Getter
    @Setter
    public static class RepetitionLevelCount implements Comparable<RepetitionLevelCount> {
        private Integer level;
        private Long count;

        @Override
        public int compareTo(RepetitionLevelCount other) {
            return this.level.compareTo(other.level);
        }
    }
}
