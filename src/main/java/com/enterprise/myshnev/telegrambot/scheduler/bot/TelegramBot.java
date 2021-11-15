package com.enterprise.myshnev.telegrambot.scheduler.bot;

import com.enterprise.myshnev.telegrambot.scheduler.commands.CommandContainer;
import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageServiceImpl;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.apache.logging.log4j.LogManager;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.SuperAdminUtils.*;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Objects;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    public static Logger LOGGER = LogManager.getLogger(TelegramBot.class);
    private  final String BOT_USER_NAME;
    private  final String TOKEN;
    private final CommandContainer commandContainer;
    private String commandIdentifier;
    private final String superAdmin;


    @Autowired
    public TelegramBot(UserService userService, WorkoutService workoutService) {
        BOT_USER_NAME = getBotConfigFromFile("botUserName".trim());
        TOKEN = getBotConfigFromFile("botToken".trim());
        superAdmin = getBotConfigFromFile("superAdmin.userId".trim());
        new ConnectionDb();
        commandContainer = new CommandContainer(new SendMessageServiceImpl(this, userService), userService, workoutService);
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
        String message;
        if (update.hasMessage() && update.getMessage().hasText()) {
            message = update.getMessage().getText().trim();
            if (message.startsWith("/")) {
                commandIdentifier = message.split(" ")[0].toLowerCase();
            } else {
                    commandIdentifier = message.split("/")[0].toLowerCase().trim();
            }
        }
        if (update.hasCallbackQuery()) {
            commandIdentifier = Objects.requireNonNull(getCallbackQuery(update)).split("/")[0];
        }
       /* if(getChatId(update).equals(superAdmin)){
             commandIdentifier = getText(update).split("/")[0];
         }*/
        commandContainer.retrieveCommand(commandIdentifier).execute(update);
    }

}
