package com.example.englingbot.service.externalapi.openai;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.Chat;
import com.example.englingbot.model.Message;
import com.example.englingbot.repository.ChatRepository;
import com.example.englingbot.service.ChatService;
import com.example.englingbot.service.externalapi.openai.enums.Role;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class TutorService extends ChatGpt{
    private final ChatService chatService;

    public TutorService(WebClient webClient, Gson gson, ChatService chatService) {
        super(webClient, gson);
        this.chatService = chatService;
    }

    public List<Message> addSystemMessage (List<Message> messages) {
        String promtForSystem = "You are an AI English tutor. Your primary role is to help users improve their " +
                "English skills through conversation. You must always respond in clear, correct, and simple English, " +
                "regardless of the language the user is speaking. You can engage in discussions on a wide range of topics, " +
                "providing educational value, corrections, and explanations on English language usage when necessary.";

        Message message = Message
                .builder()
                .role(Role.SYSTEM)
                .content(promtForSystem)
                .build();

        messages.add(0, message);

        return messages;
    }

    public String sendMessage (AppUser appUser, String messageText) {
        Chat chat = getChat(appUser);

        Message message = Message.builder()
                .chat(chat)
                .role(Role.USER)
                .content(messageText)
                .build();

        var chatHistory = chat.getMessages();
        chatHistory.add(message);

        String responseString = chat(chatHistory);

        Message responseMessage = Message.builder()
                .chat(chat)
                .role(Role.ASSISTANT)
                .content(responseString)
                .build();

        chatHistory.add(responseMessage);

        chatService.save(chat);

        return responseString;
    }

    private Chat getChat (AppUser appUser) {
        var chatOpt = chatService.getActiveChat(appUser);

        return chatOpt.orElseGet(() -> getNewChat(appUser));
    }

    public Chat getNewChat (AppUser appUser) {
        var oldChat = chatService.getActiveChat(appUser);

        oldChat.ifPresent(chat -> {
            chat.setActive(false);
            chatService.save(chat);
        });

        String firstMessage = "Hello!\n" +
                "\n" +
                "I'm your AI English tutor, excited to start our language journey. We'll have conversations on your favorite topics, and I'll guide you with tips and corrections. Share your interests and goals anytime, and let's make learning fun!\n" +
                "\n" +
                "Ready when you are!";

        Chat chat = Chat.builder()
                .appUser(appUser)
                .isActive(true)
                .messages(addSystemMessage(Collections.emptyList()))
                .build();

        Message message = Message.builder()
                .chat(chat)
                .role(Role.ASSISTANT)
                .content( firstMessage)
                .build();

        chat.getMessages().add(message);
        chatService.save(chat);

        return chat;
    }
}
