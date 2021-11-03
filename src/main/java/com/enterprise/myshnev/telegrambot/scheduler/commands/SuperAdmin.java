package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class SuperAdmin implements Command {
    private final SendMessageService sendMessageService;
    private final WorkoutService workoutService;
    private final UserService userService;
    private final UserTable userTable;
    private String firstNameCoach;
    private String lastNameCoach;
    private String chatIdCoach;
    private final String idSuperAdmin;

    public SuperAdmin(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.workoutService = workoutService;
        this.userService = userService;
        userTable = new UserTable();
        idSuperAdmin = getIdSuperAdminFromFileConfig();
    }

    @Override
    public void execute(Update update) {
        String command = getText(update);
        if (command.equals(ADD_COACH.getCommandName()) && !getChatId(update).equals(idSuperAdmin)) {
            firstNameCoach = getFirstName(update);
            lastNameCoach = getLastName(update);
            chatIdCoach = getChatId(update);
            userService.findByChatId(USERS.getTableName(),chatIdCoach,userTable).map(m -> (TelegramUser) m).ifPresent(p->{
                if(p.isCoach()){
                    sendMessageService.deleteMessage(chatIdCoach,getMessageId(update));
                }else {
                    sendMessageService.sendMessage(getChatId(update), "Ожидайте подтверждения...", null);
                    String message = "Подтвердите тренера <i>" + firstNameCoach + " " + lastNameCoach + "</i>";
                    InlineKeyboardMarkup board = builder().add("Подтвердить", "confirm_coach/ok/" + firstNameCoach + "/" + lastNameCoach).add("Отменить", "confirm_coach/no").create();
                    sendMessageService.sendMessage(idSuperAdmin, message, board);
                }
            });
        }
        if (update.hasCallbackQuery()) {
            command = Objects.requireNonNull(getCallbackQuery(update)).split("/")[0];
            if (command.equals(CONFIRM_COACH.getCommandName())) {
                String answer = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
                if (answer.equals("ok")) {
                    userService.findByChatId(USERS.getTableName(), chatIdCoach,userTable).stream().map(m -> (TelegramUser) m).findFirst()
                           .ifPresent(p -> {
                        workoutService.update(userTable, USERS.getTableName(), p.getChatId(), "coach", "1");
                        sendMessageService.editMessage(getChatId(update),getMessageId(update),"Successful!",null);
                        sendMessageService.sendMessage(p.getChatId(), "Вы добавлены тренером!", null);
                    });
                }else {
                    if(answer.equals("no")){
                        sendMessageService.deleteMessage(getChatId(update),getMessageId(update));
                        sendMessageService.sendMessage(chatIdCoach,"Отклонено!",null);
                    }
                }
            }
        }
    }

    private String getIdSuperAdminFromFileConfig() {
        Properties properties = new Properties();
        try {
            String path = System.getProperty("user.dir") + File.separator + "config.properties";
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.getMessage();
        }
        return properties.getProperty("superAdmin.userId");
    }
}
