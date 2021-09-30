package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageServiceImpl;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SuperAdmin implements Command{
    private final SendMessageService sendMessageService;

    public SuperAdmin(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {

    }
}
