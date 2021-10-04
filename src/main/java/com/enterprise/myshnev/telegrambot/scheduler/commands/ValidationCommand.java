package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class ValidationCommand implements Command{
    private final SendMessageService sendMessageService;
    private final UserService userService;

    public ValidationCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        if(update.hasCallbackQuery()){

        }
    }
}
