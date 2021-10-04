package com.enterprise.myshnev.telegrambot.scheduler.servises;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.passport.PassportData;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class SendMessageServiceImpl implements SendMessageService {
    private final TelegramBot telegramBot;

    @Autowired
    public SendMessageServiceImpl(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public Integer sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);

        try {
            return telegramBot.execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void sendMessage(String chatId, List<String> messages) {
        if (isEmpty(messages)) return;

        messages.forEach(m -> sendMessage(chatId, m));

    }

    @Override
    public Integer sendMessage(String chatId, String message, InlineKeyboardMarkup keyBoard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyBoard);
        try {
            return telegramBot.execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void editMessage(String chatId, Integer messageId, String message, InlineKeyboardMarkup keyBoard) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(message);
        editMessage.setReplyMarkup(keyBoard);
        try {
            telegramBot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editMessage(String chatId, Integer messageId, String message) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(message);
        try {
            telegramBot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
