package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.AdminTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.StatisticTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Statistic;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import java.io.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.STATISTIC;

public class StatInfoCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    public static Logger LOGGER = LogManager.getLogger(StatInfoCommand.class);
    private  static String FILE_PATH;

    public StatInfoCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        FILE_PATH = System.getProperty("user.dir") + File.separator + "statistic.csv";
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId("Admin",getChatId(update),new AdminTable()).ifPresentOrElse(p->{
        try (BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH), "windows-1251"))) {
            fileWriter.write("SEP=;");
            fileWriter.newLine();
            fileWriter.write("id;User Name;Workout;Action;Date");
            userService.findAll(STATISTIC.getTableName(), new StatisticTable()).stream().map(m -> (Statistic) m)
                    .forEach(s -> {
                        String line = String.format("%s;%s;%s;%s;%s",
                                s.getId(), s.getUserName(), s.getWorkout(), s.getAction(), s.getDate());
                        try {
                            fileWriter.newLine();
                            fileWriter.write(line);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessageService.sendDocument(getChatId(update), new File(FILE_PATH) );
    },()-> sendMessageService.deleteMessage(getChatId(update),getMessageId(update)));
    }
}
