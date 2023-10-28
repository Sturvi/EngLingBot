package com.example.englingbot.service.externalapi.openai.enums;

public enum ChatGptPromptsEnum {

    NEW_WORD("\"Identify the language of the word '%s' and provide each translation into the other language (English if the word is in Russian, Russian if the word is in English) separately. If there are multiple common translations, provide only the top five most commonly used. Each translation should be provided in a separate sentence in the following format: 'ru: {Russian word}, en: {English word}. ru: {Russian word}, en: {English word}. ru: {Russian word}, en: {English word} '."),
    TRANSCRIPTIONS("Пришли транскрипцию к слову %s. Пусть начинается с символа [ и заканчивается символом ] . больше ничего не пиши"),
    CONTEXT("Пришли контекст слова {%s} на русском максимум в 250 символов"),
    USAGE_EXAMPLES("Пришли 5 примеров использования слова %s. Примеры должны быть составлены из простых слов и понятны людям начинающим изучать язык. больше ничего не пиши. Ответ должен полностью соответствовать шаблону. Между переводами символ \n. перед новой фразой символ \n\n. Все должно быть написано в одну строку. Например: I need to purchase additional supplies \nМне нужно купить дополнительные принадлежности \n\n The hotel charges extra for additional guests $$nОтель берет дополнительную плату за дополнительных гостей \n\ne will need additional time to finish the project \nНам понадобится дополнительное время, чтобы завершить проект."),
    QUESTION("Ты в роли репетитора английского языка. Меня интересует следующий вопрос: {%s} . Если вопрос не связан с изучением языка, ответь что можешь отвечать только на вопросы связанные с изучением языка"),
    WORD_REVIEW("Есть ли грамматическая ошибка в слове '%s'.  Твой ответ должен должен соответствовать шаблону 'true' если нет грамматических ошибок. Или 'false - небольшое описание ошибки'");

    private final String prompt;

    ChatGptPromptsEnum(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
