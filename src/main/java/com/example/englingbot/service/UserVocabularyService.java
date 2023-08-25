package com.example.englingbot.service;

import com.example.englingbot.model.mapper.UserVocabularyMapper;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.repository.UserVocabularyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVocabularyService {

    private final UserVocabularyRepository userVocabularyRepository;
    private final UserVocabularyMapper userVocabularyMapper;
    private final Random random = new Random();

    /**
     * Retrieves user vocabularies by user and word state types.
     *
     * @param user  the user to search for
     * @param types the word state types to filter by
     * @return a list of user vocabularies
     */
    public List<UserVocabulary> getUserVocabularies(AppUser user, UserWordState... types) {
        log.trace("Entering getUserVocabularies method");
        List<UserVocabulary> userVocabularies = userVocabularyRepository.findByUserAndListTypeIn(user, Arrays.asList(types));
        log.debug("Retrieved user vocabularies: {}", userVocabularies);
        log.trace("Exiting getUserVocabularies method");
        return userVocabularies;
    }

    public Optional<UserVocabulary> getRandomUserVocabulary(AppUser user, UserWordState... types) {
        log.trace("Entering getRandomUserVocabulary method");
        List<UserVocabulary> userVocabularies = getUserVocabularies(user, types);
        if (userVocabularies.isEmpty()) {
            log.debug("No user vocabularies found");
            return Optional.empty();
        }

        userVocabularies = userVocabularies.stream()
                .filter(u -> u.getUpdatedAt().plusDays(u.getTimerValue()).isBefore(LocalDateTime.now()))
                .toList();

        if (userVocabularies.isEmpty()) {
            log.debug("No user vocabularies found");
            return Optional.empty();
        }

        int randomIndex = random.nextInt(userVocabularies.size());
        UserVocabulary randomUserVocabulary = userVocabularies.get(randomIndex);
        log.debug("Selected random user vocabulary: {}", randomUserVocabulary);

        log.trace("Exiting getRandomUserVocabulary method");
        return Optional.ofNullable(randomUserVocabulary);
    }

    /**
     * Adds a word to user vocabulary.
     *
     * @param word    the word to add
     * @param appUser the user to whom the word will be added
     */
    public void addWordToUserVocabulary(Word word, AppUser appUser) {
        log.trace("Mapping new word in user word list");
        var newWordInUserWordList = userVocabularyMapper.mapNewWordInUserWordList(word, appUser);
        log.debug("Mapped new word: {}", newWordInUserWordList);

        log.trace("Saving new word in user vocabulary repository");
        userVocabularyRepository.save(newWordInUserWordList);
        log.debug("Saved new word in user vocabulary repository");
    }

    @Transactional
    public void deleteWordFromUserVocabulary (Word word, AppUser appUser){
        log.trace("Deleting word from user vocabulary: {}", word);
        userVocabularyRepository.deleteByUserAndWord(appUser, word);
        log.debug("Word deleted successfully");
    }

    public Optional<String> getMessageText (Optional<UserVocabulary> userWordOpt){
        if (userWordOpt.isEmpty()) {
            return Optional.empty();
        } else {
            String messageText = getWordWithStatus(userWordOpt.get());
            log.debug("Message text: {}", messageText);
            return messageText.describeConstable();
        }
    }

    /**
     * Updates the user vocabulary for a given user and word.
     *
     * @param appUser the user whose vocabulary will be updated
     * @param word    the word to update
     */
    public void updateUserVocabulary(AppUser appUser, Word word) {
        log.trace("Checking user vocabulary for word: {}", word);

        var userVocabulary = userVocabularyRepository.findByUserAndWord(appUser, word);
        log.debug("Retrieved user vocabulary: {}", userVocabulary);

        if (userVocabulary.getTimerValue() == 0) {
            log.debug("Timer value is 0. Setting list type to REPETITION.");
            userVocabulary.setListType(UserWordState.REPETITION);
        }

        userVocabulary.setTimerValue(userVocabulary.getTimerValue() + 1);
        log.debug("Incremented timer value to: {}", userVocabulary.getTimerValue());

        userVocabularyRepository.save(userVocabulary);
        log.debug("Saved user vocabulary: {}", userVocabulary);
    }

    /**
     * Sets the learned state for a given user and word.
     *
     * @param appUser the user whose vocabulary will be updated
     * @param word    the word to set as learned
     */
    public void setLearnedState(AppUser appUser, Word word) {
        log.trace("Updating user vocabulary for word: {}", word);

        var userVocabulary = userVocabularyRepository.findByUserAndWord(appUser, word);
        log.debug("Retrieved user vocabulary: {}", userVocabulary);

        userVocabulary.setListType(UserWordState.LEARNED);
        log.debug("Set list type to LEARNED");

        userVocabulary.setTimerValue(userVocabulary.getTimerValue() + 1);
        log.debug("Incremented timer value to: {}", userVocabulary.getTimerValue());

        userVocabularyRepository.save(userVocabulary);
        log.debug("Saved updated user vocabulary");
    }

    public List<Word> getUserWordListByType(AppUser appUser, UserWordState... types) {
        var userVocabularyList = userVocabularyRepository.findByUserAndListTypeIn(appUser, List.of(types));

        return userVocabularyList.stream()
                .map(UserVocabulary::getWord)
                .toList();
    }

    public String getUserStatistics(AppUser user) {
        log.trace("Fetching statistics for user: {}", user.getId());

        StringBuilder statistics = new StringBuilder();

        long learningCount = userVocabularyRepository.countByUserAndListType(user, UserWordState.LEARNING);
        long learnedCount = userVocabularyRepository.countByUserAndListType(user, UserWordState.LEARNED);
        statistics.append("Слова на изучении: ").append(learningCount).append("\n");

        Optional<Integer> maxTimerValueOpt = userVocabularyRepository.findTopTimerValueByUserAndListType(user, UserWordState.REPETITION);

        int maxTimerValue = maxTimerValueOpt.orElse(0);
        for (int i = 1; i <= maxTimerValue; i++) {
            long repetitionCount = userVocabularyRepository.countByUserAndListTypeAndTimerValue(user, UserWordState.REPETITION, i);

            if (repetitionCount != 0) {
                statistics.append("Слова на повторении ").append(i).append(" уровня: ").append(repetitionCount).append("\n");
            }

        }

        statistics.append("Изученные слова: ").append(learnedCount);

        log.trace("Statistics for user {} fetched successfully", user.getId());

        return statistics.toString();
    }

    public List<UserVocabulary> getUserVocabularies(AppUser user, List<Word> words) {
        return userVocabularyRepository.findByUserAndWordIn(user, words);
    }

    /**
     * Generates a string representation of the user word list.
     *
     * @param userVocabulary the UserWordList object
     * @return a string representation of the UserWordList
     */
    private String getWordWithStatus(UserVocabulary userVocabulary) {
        log.trace("Entering getWordWithStatus method");

        StringBuilder sb = new StringBuilder();
        sb.append("Слово из словаря \"");

        switch (userVocabulary.getListType()) {
            case LEARNING -> sb.append("Изучаемые слова");
            case REPETITION -> sb.append("Слова на повторении ").append(userVocabulary.getTimerValue()).append(" уровня");
            case LEARNED -> sb.append("Изученное слово");
            default -> {
                log.error("Invalid list type encountered: " + userVocabulary.getListType());
            }
        }

        sb.append("\"\n\n");

        Word word = userVocabulary.getWord();
        log.trace("Got the word from userVocabulary");

        var transcription = word.getTranscription() == null ? "" : word.getTranscription();
        var englishWord = word.getEnglishWord() + " " + transcription;
        var russianWord = word.getRussianWord();

        boolean showEnglishFirst = random.nextBoolean();
        if (showEnglishFirst) {
            sb.append(englishWord)
                    .append(" - ")
                    .append(formatSpoiler(russianWord));
        } else {
            sb.append(russianWord)
                    .append("   -   ")
                    .append(formatSpoiler(englishWord));
        }

        log.trace("Returning the formatted word");
        return sb.toString();
    }

    private String formatSpoiler(String content) {
        return "<span class='tg-spoiler'>" + content + "</span>";
    }

    public List<AppUser>  getAppUserListByWord (Word word) {
        return userVocabularyRepository.findUsersByWord(word);
    }
}
