package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.MessageIdTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.MessageId;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;


import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class SendMessageServiceImpl implements SendMessageService {
    private final TelegramBot telegramBot;
    private final Map<String, List<Data>> messageId;
    private List<Data> dataList;
    private final UserService userService;
    private final MessageIdTable messageIdTable;

    @Autowired
    public SendMessageServiceImpl(TelegramBot telegramBot, UserService userService) {
        this.telegramBot = telegramBot;
        this.userService = userService;
        messageId = new HashMap<>();
        messageIdTable = new MessageIdTable();
        if (!userService.findAll(MESSAGE_ID.getTableName(), messageIdTable).isEmpty()) {
            List<MessageId> list = userService.findAll(MESSAGE_ID.getTableName(), messageIdTable).stream().map(m -> (MessageId) m).collect(Collectors.toList());
            for (MessageId messageId : list) {
                List<Data> listOfData = new ArrayList<>();
                list.stream().filter(f -> (f.getChatId().equals(messageId.getChatId()))).forEach(m -> {
                    Data data = new Data();
                    data.setChatId(m.getChatId());
                    data.setMessageId(m.getMessageId());
                    data.setTimeWorkout(m.getTime());
                    data.setDayOfWeek(m.getDayOfWeek());
                    data.setMaxUser(8);
                    listOfData.add(data);
                    this.messageId.put(m.getChatId(), listOfData);
                });
            }
        }
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
            e.printStackTrace();
        }
    }

    @Override
    public Integer sendMessage(Data data) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(data.getMessage());
        sendMessage.setChatId(data.getChatId());
        Integer id;
        if (data.getKeyBoard() != null)
            sendMessage.setReplyMarkup(data.getKeyBoard());
        try {
            id = telegramBot.executeAsync(sendMessage).get().getMessageId();
            data.setMessageId(id);
            if (!messageId.containsKey(data.getChatId())) {
                dataList = new ArrayList<>();
                dataList.add(data);
                messageId.put(data.getChatId(), dataList);
            } else {
                messageId.get(data.getChatId()).add(data);
            }
        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            id = 0;
        }
        userService.save(MESSAGE_ID.getTableName(), new MessageId(id, data.getChatId(), data.getTimeWorkout(), data.getDayOfWeek()), messageIdTable);
        return id;
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
            e.printStackTrace();
        }
    }



    @Override
    public List<Data> getData(String chatId) {
        return messageId.getOrDefault(chatId, List.of());
    }

    @Override
    public boolean deleteWorkoutMessage(String chatId, Integer messageId) {
        Boolean isDelete;
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        try {
            isDelete = telegramBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            isDelete = false;
        }
        deleteMessageId(chatId, messageId);
        return isDelete;
    }

    @Override
    public void deleteMessageId(String chatId, Integer messageId) {
        if (this.messageId.containsKey(chatId)) {
            if (this.messageId.get(chatId).stream().anyMatch(d -> (d.getMessageId().equals(messageId)))) {
                this.messageId.get(chatId).removeIf(d -> (d.getMessageId().equals(messageId)));
                userService.delete(MESSAGE_ID.getTableName(), messageId.toString(), messageIdTable);
            }
        }
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
