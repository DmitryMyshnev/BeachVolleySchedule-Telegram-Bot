package com.enterprise.myshnev.telegrambot.scheduler.servises;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public interface SendMessageService {
    void sendMessage(String chatId, String message);

    void sendMessage(String chatId, List<String> message);

    void sendMessage(String chatId, String message, InlineKeyboardMarkup keyBoard);

    void editMessage(String chatId, String messageId, String message, InlineKeyboardMarkup keyBoard);
}
