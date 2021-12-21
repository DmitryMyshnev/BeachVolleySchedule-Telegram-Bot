package com.enterprise.myshnev.telegrambot.scheduler.bot;

import com.enterprise.myshnev.telegrambot.scheduler.commands.CommandContainer;
import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.servises.ReceiveMessage;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageServiceImpl;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.apache.logging.log4j.LogManager;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.SuperAdminUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    public static Logger LOGGER = LogManager.getLogger(TelegramBot.class);
    private static final int PRIORITY_FOR_RECEIVER = 3;
    private final String BOT_USER_NAME;
    private final String TOKEN;
    public final ReceiveMessage receiveMessage;
    public final Queue<Update> receiveQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Message> notifyMessageId = new ConcurrentLinkedQueue<>();
    private static TelegramBot telegramBot;
    private final AtomicReference<String> filterQuery = new AtomicReference<>();

    @Autowired
    public TelegramBot(UserService userService, WorkoutService workoutService) {
        BOT_USER_NAME = getBotConfigFromFile("botUserName".trim());
        TOKEN = getBotConfigFromFile("botToken".trim());

        new ConnectionDb();
        CommandContainer commandContainer = new CommandContainer(new SendMessageServiceImpl(this, userService), userService, workoutService);
        receiveMessage = new ReceiveMessage(this, commandContainer);
        Thread receiver = new Thread(receiveMessage);
        receiver.setDaemon(true);
        receiver.setName("MsgReceiver");
        receiver.setPriority(PRIORITY_FOR_RECEIVER);
        receiver.start();
        telegramBot = this;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_USER_NAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = getChatId(update);
        if (update.hasCallbackQuery()) {
            if (filterQuery.get() != null) {
                if (!filterQuery.toString().equals(getChatId(update) + getCallbackQuery(update))) {
                    filterQuery.set(getChatId(update) + getCallbackQuery(update));
                    Objects.requireNonNull(sendSystemMessage(chatId)).thenAccept(res -> {
                        if (res != null) {
                            notifyMessageId.add(res);
                            receiveQueue.add(update);
                        }
                    });
                }
            } else {
                filterQuery.set(getChatId(update) + getCallbackQuery(update));
                Objects.requireNonNull(sendSystemMessage(chatId)).thenAccept(res -> {
                    if (res != null) {
                        notifyMessageId.add(res);
                        receiveQueue.add(update);
                    }
                });
            }
        } else {
            receiveQueue.add(update);
        }

    }

    private CompletableFuture<Message> sendSystemMessage(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("в обработке...⏳");
        sendMessage.setChatId(chatId);
        try {
            return executeAsync(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.info(e.getMessage());
            return null;
        }
    }

    public static TelegramBot getInstance() {
        return telegramBot;
    }

}
