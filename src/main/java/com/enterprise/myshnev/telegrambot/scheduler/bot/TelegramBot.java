package com.enterprise.myshnev.telegrambot.scheduler.bot;

import com.enterprise.myshnev.telegrambot.scheduler.commands.CommandContainer;
import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageServiceImpl;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    public static Logger LOGGER = LogManager.getLogger(TelegramBot.class);
  //  @Value("${botUserName}")
    private   String BOT_USER_NAME;
  //  @Value("${botToken}")
    private   String TOKEN;
    private final CommandContainer commandContainer;
    private String commandIdentifier;
   // @Value("${superAdmin.userId}")
    private String superAdmin;


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
       /*  if(getChatId(update).equals(superAdmin)){
             commandIdentifier = getText(update).split("/")[0];
         }*/
        commandContainer.retrieveCommand(commandIdentifier).execute(update);
    }
    private  String getBotConfigFromFile(String param) {
        Properties properties = new Properties();
        try {
            String path = System.getProperty("user.dir") + File.separator + "config.properties";
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOGGER.info( e.getMessage());
        }
        String p = properties.getProperty(param);
        return properties.getProperty(param);
    }
}
