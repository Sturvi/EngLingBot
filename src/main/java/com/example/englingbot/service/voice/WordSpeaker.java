package com.example.englingbot.service.voice;

import com.example.englingbot.model.Word;

import com.example.englingbot.service.externalapi.microsoftapi.MicrosoftTtsService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class WordSpeaker {
    private File directory;
    private final MicrosoftTtsService microsoftTtsService;


    public File getVoice(Word word) throws IOException {
        File voice = new File(directory, word.getEnglishWord() + ".wav");

        if (!voice.exists()) {
            microsoftTtsService.textToSpeech(word.getEnglishWord(), voice.toPath());
        }

        return voice;
    }

    @PostConstruct
    private void getFreeVoiceDirectory() {
        int i = 0;

        while (true) {
            String dirName = i == 0 ? "voice" : "voice" + i;
            File tempDirectory = new File(dirName);

            if (!tempDirectory.exists() || tempDirectory.isDirectory()) {
                tempDirectory.mkdirs();
                directory = tempDirectory;
                return;
            } else {
                i++;
            }
        }
    }
}
