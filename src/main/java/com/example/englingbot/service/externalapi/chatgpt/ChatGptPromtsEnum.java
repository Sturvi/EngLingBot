package com.example.englingbot.service.externalapi.chatgpt;

public enum ChatGptPromtsEnum {

    NEWWORD ("\"Identify the language of the word '{%s}' and provide each translation into the other language (English if the word is in Russian, Russian if the word is in English) separately. If there are multiple common translations, provide only the top five most commonly used. Each translation should be provided in a separate sentence in the following format: 'ru: {Russian word}, en: {English word}. ru: {Russian word}, en: {English word}. ru: {Russian word}, en: {English word}. '.");

    ChatGptPromtsEnum(String s) {

    }
}
