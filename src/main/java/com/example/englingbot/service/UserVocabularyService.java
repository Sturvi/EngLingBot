package com.example.englingbot.service;

import com.example.englingbot.mapper.UserVocabularyMapper;
import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.repository.UserVocabularyRepository;
import com.example.englingbot.service.message.MessageService;
import com.example.englingbot.service.message.TemplateMessagesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVocabularyService {

    private final UserVocabularyRepository userVocabularyRepository;
    private final MessageService messageService;
    private final TemplateMessagesSender templateMessagesSender;
    private final UserVocabularyMapper userVocabularyMapper;

    /**
     * Retrieves user vocabularies by user and word state types.
     *
     * @param user  the user to search for
     * @param types the word state types to filter by
     * @return a list of user vocabularies
     */
    public List<UserVocabulary> getUserVocabularies(AppUser user, UserWordState... types) {
        log.debug("Getting word lists for user id: {} with list types: {}", user.getId(), Arrays.toString(types));
        return userVocabularyRepository.findByUserAndListTypeIn(user, Arrays.asList(types));
    }

    /**
     * Retrieves a random user vocabulary by user and word state types.
     *
     * @param user  the user to search for
     * @param types the word state types to filter by
     * @return a random user vocabulary
     */
    public UserVocabulary getRandomUserVocabulary(AppUser user, UserWordState... types) {
        log.debug("Getting random word list for user id: {} with list types: {}", user.getId(), Arrays.toString(types));
        List<UserVocabulary> userVocabularies = getUserVocabularies(user, types);
        if (userVocabularies.isEmpty()) {
            log.warn("User id: {} has no word lists of the requested types", user.getId());
            return null;
        }
        // TODO Random можно вынести в поле сервиса
        Random rand = new Random();
        return userVocabularies.get(rand.nextInt(userVocabularies.size()));
    }

    /**
     * Generates a string representation of the user word list.
     *
     * @param userVocabulary the UserWordList object
     * @return a string representation of the UserWordList
     */
    public String getWordWithStatus(UserVocabulary userVocabulary) {
        log.debug("Generating string for word list id: {}", userVocabulary.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("Слово из словаря \"");

        switch (userVocabulary.getListType()) {
            case LEARNING -> sb.append("Изучаемые слова");
            case REPETITION ->
                    sb.append("Слова на повторении ").append(userVocabulary.getTimerValue()).append(" уровня");
            case LEARNED -> sb.append("Изученное слово");
        }

        sb.append("\"\n\n");

        Word word = userVocabulary.getWord();

        // Randomly generate the word format
        // TODO опять создаём рандом, можно в поле вынести и создать 1 раз
        Random rand = new Random();
        if (rand.nextBoolean()) {
            sb.append(word.getEnglishWord())
                    .append(" [")
                    .append(word.getTranscription())
                    .append("]   -   <span class='tg-spoiler'>")
                    .append(word.getRussianWord())
                    .append("</span>");
        } else {
            sb.append(word.getRussianWord())
                    .append("   -   <span class='tg-spoiler'>")
                    .append(word.getEnglishWord())
                    .append(" [")
                    .append(word.getTranscription())
                    .append("]</span>");
        }

        log.debug("Generated string for word list id: {}", userVocabulary.getId());
        return sb.toString();
    }

    /**
     * Adds a word to user vocabulary.
     *
     * @param word    the word to add
     * @param appUser the user to whom the word will be added
     */
    public void addWordToUserVocabulary(Word word, AppUser appUser) {
        var newWordInUserWordList = userVocabularyMapper.mapNewWordInUserWordList(word, appUser);

        userVocabularyRepository.save(newWordInUserWordList);
    }

    /**
     * Sends a random word to the user.
     *
     * @param chatId  the chat ID to send the word to
     * @param appUser the user to whom the word will be sent
     * @param types   the word state types to filter by
     */
    public void sendRandomWord(Long chatId, AppUser appUser, UserWordState... types) {
        var userWord = getRandomUserVocabulary(appUser, types);

        if (userWord == null) {
            templateMessagesSender.sendNoWordToSendMessage(chatId, types);
        } else {
            String messageText = getWordWithStatus(userWord);
            messageService.sendAudioWithWord(chatId, userWord, messageText);
        }
    }

    /**
     * Updates the user vocabulary for a given user and word.
     *
     * @param appUser the user whose vocabulary will be updated
     * @param word    the word to update
     */
    public void updateUserVocabulary(AppUser appUser, Word word) {
        var userVocabulary = userVocabularyRepository.findByUserAndWord(appUser, word);

        if (userVocabulary.getTimerValue() == 0) {
            userVocabulary.setListType(UserWordState.REPETITION);
        }

        userVocabulary.setTimerValue(userVocabulary.getTimerValue() + 1);

        userVocabularyRepository.save(userVocabulary);
    }

    /**
     * Sets the learned state for a given user and word.
     *
     * @param appUser the user whose vocabulary will be updated
     * @param word    the word to set as learned
     */
    public void setLearnedState(AppUser appUser, Word word) {
        var userVocabulary = userVocabularyRepository.findByUserAndWord(appUser, word);

        userVocabulary.setListType(UserWordState.LEARNED);
        userVocabulary.setTimerValue(userVocabulary.getTimerValue() + 1);

        userVocabularyRepository.save(userVocabulary);
    }
}
