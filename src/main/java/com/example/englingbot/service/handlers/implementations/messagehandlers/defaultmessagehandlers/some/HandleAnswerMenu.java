package com.example.englingbot.service.handlers.implementations.messagehandlers.defaultmessagehandlers.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserStateEnum;
import com.example.englingbot.service.QuestionsForChatGptService;
import com.example.englingbot.service.externalapi.chatgpt.ChatGpt;
import com.example.englingbot.service.externalapi.chatgpt.ChatGptPromptsEnum;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.handlers.interfaces.SomeDefaultMessageHandler;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandleAnswerMenu implements SomeDefaultMessageHandler {
    private final ChatGpt chatGpt;
    private final MessageService messageService;
    private final QuestionsForChatGptService questionsForChatGptService;


    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        log.trace("Handling bot event: {}", botEvent.getId());

        String question = botEvent.getText();
        String prompt = ChatGptPromptsEnum.QUESTION.getPrompt().formatted(question);

        CompletableFuture<Message> initialMessageFuture = sendInitialMessage(botEvent);

        String response = chatGpt.chat(prompt);
        handlePreviousMessage(botEvent, initialMessageFuture);

        sendResponseMessage(botEvent, response);
        saveQuestionAndResponse(appUser, question, response);
    }

    private CompletableFuture<Message> sendInitialMessage(BotEvent botEvent) {
        log.debug("Sending initial message for bot event: {}", botEvent.getId());
        return messageService.sendMessageToUser(botEvent.getId(), "Формируется ответ на ваш вопрос. Пожалуйста ожидайте...");
    }

    private void handlePreviousMessage(BotEvent botEvent, CompletableFuture<Message> messageFuture) {
        messageFuture.thenAccept(message -> {
            if (message != null) {
                messageService.deleteMessage(botEvent.getId(), message.getMessageId());
            }
        }).exceptionally(e -> {
            log.error("Error handling bot event", e);
            return null;
        });
    }

    private void sendResponseMessage(BotEvent botEvent, String response) {
        log.debug("Sending response message for bot event: {}", botEvent.getId());
        messageService.sendMessageToUser(botEvent.getId(), response);
    }

    private void saveQuestionAndResponse(AppUser appUser, String question, String response) {
        log.debug("Saving question and response for user: {}", appUser.getId());
        questionsForChatGptService.addNewQuestions(appUser, question, response);
    }

    @Override
    public UserStateEnum availableFor() {
        return UserStateEnum.ANSWER;
    }
}
