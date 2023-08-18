package com.example.englingbot.service.admin.handlers.textmessagehandler.some;

import com.example.englingbot.model.AppUser;
import com.example.englingbot.service.admin.comandsenums.AdminTextComandsEnum;
import com.example.englingbot.service.admin.handlers.textmessagehandler.SomeAdminMessageHandler;
import com.example.englingbot.service.externalapi.telegram.BotEvent;
import com.example.englingbot.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SomeAdminStartHandler implements SomeAdminMessageHandler {
    private final MessageService messageService;
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        messageService.sendMessageToAdmin(botEvent.getId(),"Выберите меню:");
    }

    @Override
    public AdminTextComandsEnum availableFor() {
        return AdminTextComandsEnum.START;
    }
}
