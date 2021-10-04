package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.AdminTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.EXIST;

public class ServiceCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;

    public ServiceCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        String message;
        if (userService.findByChatId(getChatId(update), new AdminTable()).isEmpty()) {
            message = "Введите пароль в формате:\n pass:*** \n(*** - пароль)";
        } else {
            message = "Вы и так уже админ!";
        }
        sendMessageService.sendMessage(getChatId(update), message);

    }
}
