package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.AdminTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.CoachTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Coach;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

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
        List<TelegramUser> listOfAdmin = userService.findAll(Tables.ADMIN.getTableName(), new AdminTable()).stream()
                .map(TelegramUser.class::cast).collect(Collectors.toList());

        if (command.equals(ADD_COACH.getCommandName()) &&
                !getChatId(update).equals(idSuperAdmin) &&
                listOfAdmin.stream().noneMatch(admin->(admin.getChatId().equals(getChatId(update))))) {
            firstNameCoach = getFirstName(update);
            lastNameCoach = getLastName(update);
            chatIdCoach = getChatId(update);
            userService.findByChatId(USERS.getTableName(),chatIdCoach,userTable).map(m -> (TelegramUser) m).ifPresent(p->{
                if(p.isCoach()){
                    sendMessageService.deleteMessage(chatIdCoach,getMessageId(update));
                }else {
                    sendMessageService.sendMessage(getChatId(update), "Ожидайте подтверждения...", null);
                    String message = "Подтвердите тренера <i>" + firstNameCoach + " " + lastNameCoach + "</i>";
                    InlineKeyboardMarkup board = builder().add("Подтвердить", "confirm_coach/ok/" + firstNameCoach + "/" + lastNameCoach)
                            .add("Отменить", "confirm_coach/no").create();
                    listOfAdmin.forEach(admin-> sendMessageService.sendMessage(admin.getChatId(), message, board));
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
                        userService.save(COACH.getTableName(), new Coach(p.getChatId(),firstNameCoach,lastNameCoach),new CoachTable());
                        listOfAdmin.forEach(admin-> sendMessageService.editMessage(admin.getChatId(),getMessageId(update),"Successful!",null));
                        sendMessageService.sendMessage(p.getChatId(), "Вы добавлены тренером!", null);
                    });
                }else {
                    if(answer.equals("no")){
                        listOfAdmin.forEach(admin->sendMessageService.deleteMessage(admin.getChatId(),getMessageId(update)));
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
