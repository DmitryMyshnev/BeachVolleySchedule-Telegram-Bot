package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class SendMessageServiceImpl implements SendMessageService {
    private final TelegramBot telegramBot;
    private Map<String, List<Data>> messageId;
    private List<Data> dataList;

    @Autowired
    public SendMessageServiceImpl(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        messageId = new HashMap<>();
    }

    @Override
    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);

        try {
            telegramBot.execute(sendMessage).getMessageId();

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Data data) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(data.getMessage());
        sendMessage.setChatId(data.getChatId());
        if(data.getKeyBoard() != null)
            sendMessage.setReplyMarkup(data.getKeyBoard());
        try {
            Integer id = telegramBot.executeAsync(sendMessage).get().getMessageId();
            data.setMessageId(id);
            if (!messageId.containsKey(data.getChatId())) {
                dataList = new ArrayList<>();
                dataList.add(data);
                messageId.put(data.getChatId(), dataList);
            } else {
                messageId.get(data.getChatId()).add(data);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            telegramBot.executeAsync(editMessage);
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

    @Override
    public List<Data> getData(String chatId) {
        return messageId.get(chatId);
    }
}
