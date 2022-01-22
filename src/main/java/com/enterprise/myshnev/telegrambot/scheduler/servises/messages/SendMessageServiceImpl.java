package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.model.SentMessages;
import com.enterprise.myshnev.telegrambot.scheduler.model.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;



@Service
public class SendMessageServiceImpl implements SendMessageService {
    public static Logger LOGGER = LogManager.getLogger(SendMessageServiceImpl.class);
    private final TelegramBot telegramBot;
    private final UserService userService;

    @Autowired
    public SendMessageServiceImpl(TelegramBot telegramBot, UserService userService) {
        this.telegramBot = telegramBot;
        this.userService = userService;
    }

    @Override
    public void sendMessage(String chatId, String message, InlineKeyboardMarkup keyBoard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        if (keyBoard != null) {
            sendMessage.setReplyMarkup(keyBoard);
        }
        try {
          telegramBot.execute(sendMessage).getMessageId();

        } catch (TelegramApiException e) {
            LOGGER.info(e.getMessage());
        }
    }

    @Override
    public Integer sendMessage(TelegramUser user, String message, Workout workout, InlineKeyboardMarkup board) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(user.getId());
        sendMessage.enableHtml(true);
        Integer messageId;
        if (board != null)
            sendMessage.setReplyMarkup(board);
        try {
            messageId = telegramBot.execute(sendMessage).getMessageId();
            userService.saveMessage(new SentMessages(messageId, user, workout));
        } catch (TelegramApiException  e) {
            LOGGER.info(e.getMessage());
            LOGGER.info(user + " " + workout.getTime());
            messageId = 0;
        }

        return messageId;
    }

    @Override
    public void editMessage(String chatId, Integer messageId, String message, InlineKeyboardMarkup keyBoard) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        if (message != null)
            editMessage.setText(message);
        editMessage.enableHtml(true);
        if (keyBoard != null)
            editMessage.setReplyMarkup(keyBoard);
        try {
           telegramBot.execute(editMessage);
        } catch (TelegramApiException e) {
           LOGGER.info(e.getMessage());
           LOGGER.info(chatId + " " + messageId);
        }
    }

    @Override
    public boolean deleteWorkoutMessage(String chatId, SentMessages sentMessages) {
        Boolean isDelete;
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(sentMessages.getMessageId());
        try {
            isDelete = telegramBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            LOGGER.info(e.getMessage());
            LOGGER.info(chatId);
            LOGGER.info(sentMessages.getMessageId());
            isDelete = false;
        }
        userService.deleteSentMessage(sentMessages);
        return isDelete;
    }


    @Override
    public boolean deleteMessage(String chatId, Integer messageId) {
        Boolean isDelete;
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        try {
            isDelete = telegramBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            LOGGER.info(e.getMessage());
            isDelete = false;
        }
        return isDelete;
    }

    @Override
    public void sendDocument(String chatId, File file) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        InputFile inputFile = new InputFile(file);
        sendDocument.setDocument(inputFile);
        try {
            telegramBot.execute(sendDocument);
        } catch (TelegramApiException e) {
            LOGGER.info(e.getMessage());
        }
    }
}
