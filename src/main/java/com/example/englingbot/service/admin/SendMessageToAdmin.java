package com.example.englingbot.service.admin;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.service.AppUserService;
import com.example.englingbot.service.admin.keyboards.AdminReplyKeyboardFactory;
import com.example.englingbot.service.message.MessageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMessageToAdmin {
    private final MessageService messageService;
    private final AppUserService appUserService;
    private List<AppUser> administrators;


    @PostConstruct
    private void init(){
        administrators = appUserService.getAppUserListByRole(UserRoleEnum.ADMIN);
    }

    public void sendMessageToAllAdmin (String messageText){
        var keyboard = AdminReplyKeyboardFactory.getReplyKeyboardMarkup();

        administrators.forEach(a -> messageService.sendMessageWithKeyboard(a.getTelegramChatId(), messageText, keyboard));
    }

    public void sendMessage (Long chatId, String messageText) {
        var keyboard = AdminReplyKeyboardFactory.getReplyKeyboardMarkup();

        sendMessageWithKeyboard(chatId, messageText, keyboard);
    }

    public void sendMessageWithKeyboard (Long chatId, String messageText, ReplyKeyboard keyboard) {
        messageService.sendMessageWithKeyboard(chatId, messageText, keyboard);
    }
}
