package com.example.englingbot.service;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.UserWordList;
import com.example.englingbot.model.Word;
import com.example.englingbot.model.enums.WordListTypeEnum;
import com.example.englingbot.repository.UserWordListRepository;
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

class UserWordListServiceTest {

    @Mock
    private UserWordListRepository userWordListRepository;

    @InjectMocks
    private UserWordListService userWordListService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserWordLists() {
        AppUser user = new AppUser();
        user.setId(1L);
        WordListTypeEnum[] types = {WordListTypeEnum.LEARNING, WordListTypeEnum.REPETITION};
        List<UserWordList> list = List.of(Mockito.mock(UserWordList.class), Mockito.mock(UserWordList.class), Mockito.mock(UserWordList.class));
        when(userWordListRepository.findByUserAndListTypeIn(eq(user), anyList())).thenReturn(list);

        List<UserWordList> result = userWordListService.getUserWordLists(user, types);
        assertEquals(3, result.size());
        assertEquals(list.get(0), result.get(0));
    }

    @Test
    void testGetRandomUserWordList() {
        AppUser user = new AppUser();
        user.setId(1L);
        WordListTypeEnum[] types = {WordListTypeEnum.LEARNING, WordListTypeEnum.REPETITION};

        UserWordList userWordList1 = Mockito.mock(UserWordList.class);
        userWordList1.setUser(Mockito.mock(AppUser.class));

        UserWordList userWordList2 = Mockito.mock(UserWordList.class);
        userWordList2.setUser(Mockito.mock(AppUser.class));

        UserWordList userWordList3 = Mockito.mock(UserWordList.class);
        userWordList3.setUser(Mockito.mock(AppUser.class));

        List<UserWordList> list = List.of(userWordList1, userWordList2, userWordList3);

        when(userWordListRepository.findByUserAndListTypeIn(eq(user), anyList())).thenReturn(list);

        UserWordList result = userWordListService.getRandomUserWordList(user, types);
        assertTrue(list.contains(result));
    }

    @Test
    void testGetRandomUserWordListEmpty() {
        AppUser user = new AppUser();
        user.setId(1L);
        WordListTypeEnum[] types = {WordListTypeEnum.LEARNING, WordListTypeEnum.REPETITION};
        when(userWordListRepository.findByUserAndListTypeIn(eq(user), anyList())).thenReturn(Collections.emptyList());

        UserWordList result = userWordListService.getRandomUserWordList(user, types);
        assertNull(result);
    }

    @Test
    void testGetUserWordListString() {
        UserWordList list = new UserWordList();
        list.setListType(WordListTypeEnum.LEARNING);
        list.setTimerValue(5);
        Word word = new Word();
        word.setEnglishWord("word");
        word.setRussianWord("слово");
        word.setTranscription("wəːd");
        list.setWord(word);

        String result = userWordListService.getUserWordListString(list);
        String expectedStart = "Слово из словаря \"Изучаемые слова\"\n\n";
        String expectedEndEngToRus = "word [wəːd]   -   <span class='tg-spoiler'>слово</span>";
        String expectedEndRusToEng = "слово   -   <span class='tg-spoiler'>word [wəːd]</span>";

        assertTrue(result.startsWith(expectedStart));
        assertTrue(result.endsWith(expectedEndEngToRus) || result.endsWith(expectedEndRusToEng));
    }
}
