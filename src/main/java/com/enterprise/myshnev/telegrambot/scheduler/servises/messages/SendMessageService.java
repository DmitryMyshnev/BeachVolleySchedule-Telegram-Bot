package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public interface SendMessageService {
    void sendMessage(String chatId, String message);

    void sendMessage(Data data);

    void editMessage(String chatId, Integer messageId, String message, InlineKeyboardMarkup keyBoard);

    void editMessage(String chatId, Integer messageId, String message);

    List<Data> getData(String chatId);

}
