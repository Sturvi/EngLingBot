package com.example.englingbot.service.externalapi.openai;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Chat;
import com.example.englingbot.model.Message;
import com.example.englingbot.service.ChatService;
import com.example.englingbot.service.MessageService;
import com.example.englingbot.service.externalapi.openai.enums.Role;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Service for providing tutoring features.
 */
@Service
@Slf4j
public class TutorService extends ChatGpt {
    private final ChatService chatService;
    private final MessageService messageService;

    public TutorService(WebClient webClient, Gson gson, ChatService chatService, MessageService messageService) {
        super(webClient, gson);
        this.chatService = chatService;
        this.messageService = messageService;
    }

    private void addSystemMessage(Chat chat) {
        log.trace("Entering addSystemMessage()");

        if (chat.getMessages() == null) {
            chat.setMessages(new ArrayList<>());
        }

        String promptForSystem = "You are an English tutor named Katya. Your primary role is to help users improve their " +
        "English skills through conversation. You must always respond in clear, correct, and simple English, " +
        "regardless of the language the user is speaking. You can engage in discussions on a wide range of topics, " +
        "providing educational value, corrections, and explanations on English language usage when necessary.";

        Message message = createMessage(chat, Role.SYSTEM, promptForSystem);
        chat.getMessages().add(0, message);
        messageService.save(message);

        log.trace("Exiting addSystemMessage()");
    }

    public String sendMessage(AppUser appUser, String messageText) {
        log.trace("Entering sendMessage()");

        messageText += "Your answer should always be in English.";

        Chat chat = getOrCreateChat(appUser);
        Message userMessage = createMessage(chat, Role.USER, messageText);
        chat.getMessages().add(userMessage);

        String responseText = chat(chat.getMessages());
        Message responseMessage = createMessage(chat, Role.ASSISTANT, responseText);
        chat.getMessages().add(responseMessage);

        chatService.save(chat);

        log.trace("Exiting sendMessage()");
        return responseText;
    }

    public Chat getOrCreateChat(AppUser appUser) {
        log.trace("Entering getOrCreateChat()");

        Chat chat = chatService.getActiveChat(appUser).orElseGet(() -> createNewChat(appUser));

        log.trace("Exiting getOrCreateChat()");
        return chat;
    }

    private Chat createNewChat(AppUser appUser) {
        log.trace("Entering createNewChat()");

        deactivateOldChat(appUser);

        String firstMessageContent = "Hello!\n\n" +
                "I'm your English tutor Katya, excited to start our language journey. We'll have conversations on your " +
                "favorite topics, and I'll guide you with tips and corrections. Share your interests and goals anytime, " +
                "and let's make learning fun!\n\n" +
                "Ready when you are!";
        Chat newChat = Chat.builder()
                .appUser(appUser)
                .isActive(true)
                .build();

        chatService.save(newChat);
        addSystemMessage(newChat);

        Message firstMessage = createMessage(newChat, Role.ASSISTANT, firstMessageContent);
        newChat.getMessages().add(firstMessage);
        messageService.save(firstMessage);

        chatService.save(newChat);

        log.trace("Exiting createNewChat()");
        return newChat;
    }

    private void deactivateOldChat(AppUser appUser) {
        log.trace("Entering deactivateOldChat()");

        Optional<Chat> oldChatOpt = chatService.getActiveChat(appUser);
        oldChatOpt.ifPresent(oldChat -> {
            oldChat.setActive(false);
            chatService.save(oldChat);
        });

        log.trace("Exiting deactivateOldChat()");
    }

    private Message createMessage(Chat chat, Role role, String content) {
        log.trace("Entering createMessage()");

        Message message = Message.builder()
                .chat(chat)
                .role(role)
                .content(content)
                .build();
        messageService.save(message);

        log.trace("Exiting createMessage()");
        return message;
    }
}
