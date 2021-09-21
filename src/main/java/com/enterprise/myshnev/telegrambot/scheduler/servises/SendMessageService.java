package com.enterprise.myshnev.telegrambot.scheduler.servises;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendMessageService {
    private final TelegramBot telegramBot;
@Autowired
    public SendMessageService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
}
