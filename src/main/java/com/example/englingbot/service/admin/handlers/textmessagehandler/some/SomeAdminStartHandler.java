package com.example.englingbot.service.admin.handlers.textmessagehandler.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.admin.MessageToAdmin;
import com.example.englingbot.service.admin.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.admin.handlers.textmessagehandler.SomeAdminMessageHandler;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SomeAdminStartHandler implements SomeAdminMessageHandler {
    private final MessageToAdmin message;
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        message.sendMessageToAllAdmin("Выберите меню:");
    }

    @Override
    public AdminTextComandsEnum availableFor() {
        return AdminTextComandsEnum.START;
    }
}