package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.CoachTable;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class HelpCommand implements Command{
    private final SendMessageService sendMessageService;
    private final UserService userService;
    public static Logger LOGGER = LogManager.getLogger(HelpCommand.class);
    private String message;

    public HelpCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        String time = getTimeNotificationFromFileConfig();
        message = "/start - начало работы с ботом\n" +
                "/workout - просмотреть расписание тренировок\n" +
                "/stop -  отключить уведомления\n" +
                "ℹ️\n" +
                "Запись на тренировку открывается за день до тренировки в " + time  +
                " и закрывается  за час до начала.\n";
     userService.findByChatId(COACH.getTableName(), getChatId(update),new CoachTable()).ifPresentOrElse(p->{
         message += " - вы можете отправлять сообщение всем участникам. Для этого перед сообщением поставьте символ #\n" +
                    " - вы можете удалить тренировку принудительно. Все участники  получат уведомление об отмене";
         sendMessageService.sendMessage(getChatId(update),message,null);
     },()->{
         message += "";
         sendMessageService.sendMessage(getChatId(update),message,null);
     });
    }
    private  String getTimeNotificationFromFileConfig() {
        Properties properties = new Properties();
        try {
            String path = System.getProperty("user.dir") + File.separator + "config.properties";
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return properties.getProperty("timeOfNotification");
    }
}
