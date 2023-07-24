package com.example.englingbot;

import com.example.englingbot.model.Word;
import com.example.englingbot.service.WordService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Test {

    private final WordService wordService;

    public Test(WordService wordService) {
        this.wordService = wordService;
    }


    public void test (){
        var newWordList = wordService.addNewWordFromExternalApi("земля");

        for (Word word : newWordList) {
            log.error(word.getRussianWord() + " " + word.getEnglishWord());
        }
    }
}
