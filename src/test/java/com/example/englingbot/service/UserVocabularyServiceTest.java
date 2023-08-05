package com.example.englingbot.service;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserVocabulary;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.UserWordState;
import com.example.englingbot.repository.UserVocabularyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserVocabularyServiceTest {

    @Mock
    private UserVocabularyRepository userVocabularyRepository;

    @InjectMocks
    private UserVocabularyService userVocabularyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserWordLists() {
        AppUser user = new AppUser();
        user.setId(1L);
        UserWordState[] types = {UserWordState.LEARNING, UserWordState.REPETITION};
        List<UserVocabulary> list = List.of(Mockito.mock(UserVocabulary.class), Mockito.mock(UserVocabulary.class), Mockito.mock(UserVocabulary.class));
        when(userVocabularyRepository.findByUserAndListTypeIn(eq(user), anyList())).thenReturn(list);

        List<UserVocabulary> result = userVocabularyService.getUserVocabularies(user, types);
        assertEquals(3, result.size());
        assertEquals(list.get(0), result.get(0));
    }

    @Test
    void testGetRandomUserWordList() {
        AppUser user = new AppUser();
        user.setId(1L);
        UserWordState[] types = {UserWordState.LEARNING, UserWordState.REPETITION};

        UserVocabulary userVocabulary1 = Mockito.mock(UserVocabulary.class);
        userVocabulary1.setUser(Mockito.mock(AppUser.class));

        UserVocabulary userVocabulary2 = Mockito.mock(UserVocabulary.class);
        userVocabulary2.setUser(Mockito.mock(AppUser.class));

        UserVocabulary userVocabulary3 = Mockito.mock(UserVocabulary.class);
        userVocabulary3.setUser(Mockito.mock(AppUser.class));

        List<UserVocabulary> list = List.of(userVocabulary1, userVocabulary2, userVocabulary3);

        when(userVocabularyRepository.findByUserAndListTypeIn(eq(user), anyList())).thenReturn(list);

        UserVocabulary result = userVocabularyService.getRandomUserVocabulary(user, types);
        assertTrue(list.contains(result));
    }

    @Test
    void testGetRandomUserWordListEmpty() {
        AppUser user = new AppUser();
        user.setId(1L);
        UserWordState[] types = {UserWordState.LEARNING, UserWordState.REPETITION};
        when(userVocabularyRepository.findByUserAndListTypeIn(eq(user), anyList())).thenReturn(Collections.emptyList());

        UserVocabulary result = userVocabularyService.getRandomUserVocabulary(user, types);
        assertNull(result);
    }

    @Test
    void testGetUserWordListString() {
        UserVocabulary list = new UserVocabulary();
        list.setListType(UserWordState.LEARNING);
        list.setTimerValue(5);
        Word word = new Word();
        word.setEnglishWord("word");
        word.setRussianWord("слово");
        word.setTranscription("wəːd");
        list.setWord(word);

        String result = userVocabularyService.getWordWithStatus(list);
        String expectedStart = "Слово из словаря \"Изучаемые слова\"\n\n";
        String expectedEndEngToRus = "word [wəːd]   -   <span class='tg-spoiler'>слово</span>";
        String expectedEndRusToEng = "слово   -   <span class='tg-spoiler'>word [wəːd]</span>";

        assertTrue(result.startsWith(expectedStart));
        assertTrue(result.endsWith(expectedEndEngToRus) || result.endsWith(expectedEndRusToEng));
    }
}
