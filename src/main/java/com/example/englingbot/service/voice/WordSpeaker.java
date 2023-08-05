package com.example.englingbot.service.voice;

import com.example.englingbot.model.Word;
import com.example.englingbot.service.externalapi.microsoftapi.MicrosoftTtsService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * A class responsible for handling voice files for words.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WordSpeaker {
    private File directory;
    private final MicrosoftTtsService microsoftTtsService;

    /**
     * Gets the voice file for the given word. If the file does not exist, it uses the Microsoft TTS service to create it.
     * @param word The word for which the voice file is needed.
     * @return The voice file.
     * @throws IOException If an I/O error occurs.
     */
    public File getVoice(Word word) throws IOException {
        File voice = new File(directory, word.getEnglishWord() + ".wav");

        if (!voice.exists()) {
            log.debug("Voice file does not exist for word {}. Creating it using Microsoft TTS service.", word.getEnglishWord());
            microsoftTtsService.textToSpeech(word.getEnglishWord(), voice.toPath());
        } else {
            log.debug("Voice file found for word {}", word.getEnglishWord());
        }

        return voice;
    }

    /**
     * Finds a free voice directory and creates it if necessary. This method is executed after the bean is constructed.
     */
    @PostConstruct
    private void getFreeVoiceDirectory() {
        int i = 0;

        while (true) {
            String dirName = i == 0 ? "voice" : "voice" + i;
            File tempDirectory = new File(dirName);

            if (!tempDirectory.exists() || tempDirectory.isDirectory()) {
                log.debug("Creating voice directory {}", dirName);
                tempDirectory.mkdirs();
                directory = tempDirectory;
                return;
            } else {
                i++;
            }
        }
    }
}
