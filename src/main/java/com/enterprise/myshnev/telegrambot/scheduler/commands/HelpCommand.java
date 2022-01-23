package com.enterprise.myshnev.telegrambot.scheduler.commands;

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

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.getChatId;

public class HelpCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    public static Logger LOGGER = LogManager.getLogger(HelpCommand.class);
    private String message;

    private final String SUPER_ADMIN;

    public HelpCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;

        SUPER_ADMIN = SuperAdminUtils.getInstance().getIdSuperAdminFromFileConfig();
    }

    @Override
    public void execute(Update update) {

        String time = SuperAdminUtils.getInstance().getTimeNotificationFromFileConfig();
        message = "ℹ️\n" +
                "Запись на тренировку открывается за день до тренировки в " + time +
                " и закрывается  за час до начала.\n" +
                "/workouts - просмотреть расписание тренировок\n" +
                "/stop - отключить уведомления. Вы не будете получать сообщения о начале записи на тренировку\n" +
                "/start - включить уведомления\n";

        userService.findByChatId(getChatId(update))
                .ifPresent(user -> {
                    if (user.isEqualsRole("COACH")) {
                        message += " - вы можете отправлять сообщение всем участникам. Для этого перед сообщением поставьте символ #\n" +
                                " - вы можете удалить тренировку принудительно. Все участники  получат уведомление об отмене\n";
                    }
                    if (getChatId(update).equals(SUPER_ADMIN)) {
                        message += """
                                /stat -  получить статистику
                                /db -  получить файл базы данных
                                time/ - узказать время оповещения
                                """;
                    }
                    sendMessageService.sendMessage(getChatId(update), message, null);
                });
    }


}
