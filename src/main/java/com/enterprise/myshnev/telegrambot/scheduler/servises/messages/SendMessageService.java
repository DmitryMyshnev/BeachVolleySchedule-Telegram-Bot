package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public interface SendMessageService {
    void sendMessage(String chatId, String message,InlineKeyboardMarkup keyBoard);

    Integer sendMessage(Data data);

    void editMessage(String chatId, Integer messageId, String message, InlineKeyboardMarkup keyBoard);

    List<Data> getData(String chatId);

    boolean deleteWorkoutMessage(String chatId, Integer messageId);

    boolean deleteMessage(String chatId, Integer messageId);

    void deleteMessageId(String chatId,Integer messageId);

    void  sendDocument(String chatId, File file);

}
