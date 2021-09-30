package com.enterprise.myshnev.telegrambot.scheduler.servises;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class SendMessageServiceImpl implements SendMessageService{
    private final TelegramBot telegramBot;
@Autowired
    public SendMessageServiceImpl(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String chatId, List<String> messages) {
        if (isEmpty(messages)) return;

        messages.forEach(m -> sendMessage(chatId, m));
    }

    @Override
    public void sendMessage(String chatId, String message, InlineKeyboardMarkup keyBoard) {

    }

    @Override
    public void editMessage(String chatId, String messageId, String message, InlineKeyboardMarkup keyBoard) {

    }
}
