package com.example.englingbot.service.telegrambot.admin.handlers.textmessagehandler.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.telegrambot.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.telegrambot.admin.handlers.textmessagehandler.SomeAdminMessageHandler;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.telegrambot.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SomeAdminStartHandler implements SomeAdminMessageHandler {
    private final TelegramMessageService telegramMessageService;
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        telegramMessageService.sendMessageToAdmin(botEvent.getId(),"Выберите меню:");
    }

    @Override
    public AdminTextComandsEnum availableFor() {
        return AdminTextComandsEnum.START;
    }
}
