package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.AdminTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

public class Go implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private List<Integer> messageId;

    public Go(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        String  message;
       int  total = userService.count(new AdminTable());

        message = "Запись на тренировку открыта!\nКоличество свободных мест: 8";
        userService.findAll(new UserTable()).stream().map(u -> (TelegramUser) u).collect(Collectors.toList()).forEach(user -> {
            messageId.add(sendMessageService.sendMessage(user.getChatId(), message, keyBoard()));
        });

    }

    private InlineKeyboardMarkup keyBoard() {
        return builder()
                .add("18:00", "18")
                .add("20:00", "20")
                .create();
    }
}
