package com.enterprise.myshnev.telegrambot.scheduler.bot;

import com.enterprise.myshnev.telegrambot.scheduler.commands.CommandContainer;
import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String BOT_USER_NAME = "exchange_CLI_bot";
    private static final String TOKEN = "2002904530:AAEVfsYTwAsbICA1pjuVtBYs-y9F1aCYZPA";
    private final CommandContainer commandContainer;
@Autowired
    public TelegramBot() {
        commandContainer = new CommandContainer(new SendMessageService(this));
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
    @Override
    public String getBotUsername() {
        return BOT_USER_NAME;
    }
    @Override
    public void onUpdateReceived(Update update) {
     String chatId = update.getMessage().getChatId().toString();
    }


}
