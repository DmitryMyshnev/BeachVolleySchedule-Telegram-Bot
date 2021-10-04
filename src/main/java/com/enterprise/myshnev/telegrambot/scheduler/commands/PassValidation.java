package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.AdminTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;

import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;


public class PassValidation implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private String adminPass;

    public PassValidation(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        adminPass = getPassFromFileConfig();
        String passFromUser = getText(update).split(":")[1].trim();
        TelegramUser user = new TelegramUser(getChatId(update), getFirstName(update), getLastName(update));

        if (passFromUser.equals(adminPass)) {
            userService.save(user, new AdminTable());
            sendMessageService.sendMessage(getChatId(update), "Success!", keyBoard());
        } else {
            sendMessageService.sendMessage(getChatId(update), "password fail!");
        }

    }

    private String getPassFromFileConfig() {
        Properties properties = new Properties();
        try {
            File file = ResourceUtils.getFile("classpath:application.properties");
            InputStream in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
            e.getMessage();
        }
        return properties.getProperty("admin.pass");
    }

    private InlineKeyboardMarkup keyBoard() {
        return builder()
                .add("Добавить тренера", ADD_COACH.getCommandName())
                .add("Создать тренировку", ADD_WORKOUT.getCommandName())
                .create();
    }
}
