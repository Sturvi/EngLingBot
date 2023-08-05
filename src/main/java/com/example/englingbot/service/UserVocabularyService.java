package com.example.englingbot.service;

import com.example.englingbot.mapper.UserWordListMapper;
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


    public List<UserVocabulary> getUserVocabularies(AppUser user, UserWordState... types) {
        log.info("Getting word lists for user id: {} with list types: {}", user.getId(), Arrays.toString(types));
        return userVocabularyRepository.findByUserAndListTypeIn(user, Arrays.asList(types));
    }


    public UserVocabulary getRandomUserVocabulary(AppUser user, UserWordState... types) {
        log.info("Getting random word list for user id: {} with list types: {}", user.getId(), Arrays.toString(types));
        List<UserVocabulary> userVocabularies = getUserVocabularies(user, types);
        if (userVocabularies.isEmpty()) {
            log.warn("User id: {} has no word lists of the requested types", user.getId());
            return null;
        }
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
        log.info("Generating string for word list id: {}", userVocabulary.getId());

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

        log.info("Generated string for word list id: {}", userVocabulary.getId());
        return sb.toString();
    }

    public void addWordToUserVocabulary(Word word, AppUser appUser) {
        var newWordInUserWordList = UserWordListMapper.mapNewWordInUserWordList(word, appUser);

        userVocabularyRepository.save(newWordInUserWordList);
    }

    public void sendRandomWord(Long chatId, AppUser appUser, UserWordState... types) {
        var userWord = getRandomUserVocabulary(appUser, types);

        if (userWord == null) {
            templateMessagesSender.sendNoWordToSendMessage(chatId, types);
        } else {
            String messageText = getWordWithStatus(userWord);
            messageService.sendAudioWithWord(chatId, userWord, messageText);
        }
    }

    public void updateUserVocabulary(AppUser appUser, Word word) {
        var userVocabulary = userVocabularyRepository.findByUserAndWord(appUser, word);

        if (userVocabulary.getTimerValue() == 0) {
            userVocabulary.setListType(UserWordState.REPETITION);
        }

        userVocabulary.setTimerValue(userVocabulary.getTimerValue() + 1);

        userVocabularyRepository.save(userVocabulary);
    }

    public void setLearnedState (AppUser appUser, Word word){
        var userVocabulary = userVocabularyRepository.findByUserAndWord(appUser, word);

        userVocabulary.setListType(UserWordState.LEARNED);
        userVocabulary.setTimerValue(userVocabulary.getTimerValue() + 1);

        userVocabularyRepository.save(userVocabulary);
    }
}
