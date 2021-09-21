package com.enterprise.myshnev.telegrambot.scheduler.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String BOT_USER_NAME = "exchange_CLI_bot";
    private static final String TOKEN = "2002904530:AAEVfsYTwAsbICA1pjuVtBYs-y9F1aCYZPA";
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
