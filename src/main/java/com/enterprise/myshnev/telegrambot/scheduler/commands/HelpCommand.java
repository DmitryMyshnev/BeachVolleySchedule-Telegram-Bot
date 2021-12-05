package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.AdminTable;
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

public class HelpCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    public static Logger LOGGER = LogManager.getLogger(HelpCommand.class);
    private String message;
    private final CoachTable coachTable;
    private final AdminTable adminTable;
    private final String SUPER_ADMIN;

    public HelpCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        coachTable = new CoachTable();
        adminTable = new AdminTable();
        SUPER_ADMIN = SuperAdminUtils.getIdSuperAdminFromFileConfig();
    }

    @Override
    public void execute(Update update) {

        String time = getTimeNotificationFromFileConfig();
        message = "ℹ️\n" +
                "Запись на тренировку открывается за день до тренировки в " + time +
                " и закрывается  за час до начала.\n" +
                "/workouts - просмотреть расписание тренировок\n" +
                "/stop - отключить уведомления. Вы не будете получать сообщения о начале записи на тренировку\n" +
                "/start - включить уведомления\n";

        userService.findByChatId(COACH.getTableName(), getChatId(update), coachTable)
                .ifPresent(p -> message += " - вы можете отправлять сообщение всем участникам. Для этого перед сообщением поставьте символ #\n" +
                " - вы можете удалить тренировку принудительно. Все участники  получат уведомление об отмене");
        userService.findByChatId(ADMIN.getTableName(), getChatId(update), adminTable)
                .ifPresent(admin -> message += "/stat -  получить статистику");
        sendMessageService.sendMessage(getChatId(update), message, null);
        if(getChatId(update).equals(SUPER_ADMIN)){
            message += "/stat -  получить статистику\n" +
                    "/db -  получить файл базы данных\n" +
                    "time/ - узказать время оповещения\n";
            sendMessageService.sendMessage(getChatId(update), message, null);
        }
    }

    private String getTimeNotificationFromFileConfig() {
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
