package com.example.englingbot.service.voice;

import com.example.englingbot.model.Word;
import com.example.englingbot.service.externalapi.microsoftapi.MicrosoftTtsService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * A class responsible for handling voice files for words.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VoiceSpeaker {
    private File directory;
    private final MicrosoftTtsService microsoftTtsService;


    public Optional<File> getVoice(Word word) {
        log.trace("Checking if voice file exists for word: {}", word.getEnglishWord());
        File voice = new File(directory, word.getEnglishWord() + ".wav");

        if (!voice.exists()) {
            log.debug("Voice file does not exist for word {}. Creating it using Microsoft TTS service.", word.getEnglishWord());
            microsoftTtsService.textToSpeech(word.getEnglishWord(), voice.toPath());
            log.info("Voice file created for word: {}", word.getEnglishWord());
        } else {
            log.debug("Voice file found for word: {}", word.getEnglishWord());
        }

        log.trace("Returning voice file: {}", voice.getAbsolutePath());
        return Optional.of(voice);
    }

    public Optional<File> getVoiceFileFromString(String text) {
        log.trace("Creating voice file for the string: {}", text);

        File voiceFile = microsoftTtsService.textToSpeechFile(text);

        log.trace("Voice file created for the string: {}", text);
        return Optional.of(voiceFile);
    }

    /**
     * Finds a free voice directory and creates it if necessary. This method is executed after the bean is constructed.
     */
    @PostConstruct
    private void getFreeVoiceDirectory() {
        int i = 0;
        Path dirPath;

        do {
            String dirName = i == 0 ? "voice" : "voice" + i;
            dirPath = Paths.get(dirName);
            i++;
        } while (Files.exists(dirPath) && !Files.isDirectory(dirPath));

        if (!Files.exists(dirPath)) {
            log.trace("Creating directory: {}", dirPath.toString());
            try {
                Files.createDirectories(dirPath);
                directory = dirPath.toFile();
            } catch (IOException e) {
                log.error("Error creating directory {}", dirPath, e);
            }
        } else {
            directory = dirPath.toFile();
        }
    }

}
