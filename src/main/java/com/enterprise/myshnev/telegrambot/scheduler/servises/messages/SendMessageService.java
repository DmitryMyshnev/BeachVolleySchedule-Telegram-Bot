package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public interface SendMessageService {
    void sendMessage(String chatId, String message,InlineKeyboardMarkup keyBoard);

    Integer sendMessage(String chatId, String message,String timeWorkout,String dayOfWeek,InlineKeyboardMarkup board);

    void editMessage(String chatId, Integer messageId, String message, InlineKeyboardMarkup keyBoard);

    boolean deleteWorkoutMessage(String chatId, Integer messageId);

    boolean deleteMessage(String chatId, Integer messageId);

    void  sendDocument(String chatId, File file);

}
