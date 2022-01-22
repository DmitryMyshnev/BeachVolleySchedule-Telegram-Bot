package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import com.enterprise.myshnev.telegrambot.scheduler.model.SentMessages;
import com.enterprise.myshnev.telegrambot.scheduler.model.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;

public interface SendMessageService {
    void sendMessage(String chatId, String message,InlineKeyboardMarkup keyBoard);

    Integer sendMessage(TelegramUser user, String message, Workout workout, InlineKeyboardMarkup board);

    void editMessage(String chatId, Integer messageId, String message, InlineKeyboardMarkup keyBoard);

    boolean deleteWorkoutMessage(String chatId, SentMessages sentMessages);

    boolean deleteMessage(String chatId, Integer messageId);

    void  sendDocument(String chatId, File file);

}
