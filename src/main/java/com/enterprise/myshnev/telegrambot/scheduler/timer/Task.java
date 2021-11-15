package com.enterprise.myshnev.telegrambot.scheduler.timer;

import com.enterprise.myshnev.telegrambot.scheduler.commands.SuperAdminUtils;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.Data;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.Symbols.*;
import static java.util.concurrent.TimeUnit.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.SuperAdminUtils.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class Task extends TimerTask {

    private final static Map<String, Integer> WEEK = ImmutableMap.<String, Integer>builder()
            .put("пн", 1)
            .put("вт", 2)
            .put("ср", 3)
            .put("чт", 4)
            .put("пт", 5)
            .put("сб", 6)
            .put("вс", 7)
            .put("mo",1)
            .put("tu",2)
            .put("wed",3)
            .put("thu",4)
            .put("fri",5)
            .put("sat",6)
            .put("sun",7).build();
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private final String  HOUR_OF_NOTIFICATION;
    private String message;
    private InlineKeyboardMarkup board;
    private final SimpleDateFormat formatOfWeek;
    private final StopTimerTack stopTimerTack;
    private final SimpleDateFormat formatOfHour;
    private final WorkoutsTable workoutsTable;
    private final NewWorkoutTable newWorkoutsTable;
    public static Logger LOGGER = LogManager.getLogger(Task.class);

    public Task(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        this.stopTimerTack = new StopTimerTack(sendMessageService, userService, workoutService);
        formatOfWeek = new SimpleDateFormat("u");
        formatOfHour = new SimpleDateFormat("H:mm");
        workoutsTable = new WorkoutsTable();
        newWorkoutsTable = new NewWorkoutTable();
        HOUR_OF_NOTIFICATION = SuperAdminUtils.getTimeNotificationFromFileConfig();
    }

    @Override
    public void run() {

        workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream().map(w -> (Workouts) w).forEach(w -> {
            String currentDayOfWeek = formatOfWeek.format(System.currentTimeMillis());
            int dayOfNotification = (WEEK.get(w.getDayOfWeek()) - 1) == 0 ? 7 : WEEK.get(w.getDayOfWeek()) - 1;
           //int dayOfNotification = WEEK.get(w.getDayOfWeek());
            int dayOfWorkout = WEEK.get(w.getDayOfWeek());
            if (Integer.parseInt(currentDayOfWeek) == dayOfNotification && formatOfHour.format(System.currentTimeMillis()).equals(HOUR_OF_NOTIFICATION)) {
                workoutService.update(workoutsTable, WORKOUT.getTableName(), w.getId().toString(), "active", "1");
                createNotification(w.getDayOfWeek(), w.getTime(), w.getMaxCountUser());
            }

            if (Integer.parseInt(currentDayOfWeek) == dayOfWorkout &&  formatOfHour.format(System.currentTimeMillis() + HOURS.toMillis(1)).equals(w.getTime())) {
                stopTimerTack.breakWorkout(w.getDayOfWeek(), w.getTime(), w.getId());

            }
        });
    }

    private void createNotification(String dayOfWeek, String time, Integer maxPlayer) {
        Locale locale = new Locale("ru");
        String date =  new SimpleDateFormat("E d MMM",locale).format(System.currentTimeMillis() + DAYS.toMillis(1));
        String callback = ENJOY.getCommandName() + "/" + dayOfWeek + "/" + time + "/" + maxPlayer;
        message = "Запись на тренировку в " + date + " в " + time + " открыта!\nКоличество свободных мест:    " + getSymbol(maxPlayer);
        workoutService.addTable(dayOfWeek + time, newWorkoutsTable);
        userService.findAll(USERS.getTableName(), new UserTable()).stream().map(u -> (TelegramUser) u).collect(Collectors.toList()).forEach(user -> {
            if (user.isCoach()) {
                board = builder().add("Отменить тренировку", "cancel_workout/" + dayOfWeek + "/" + time).create();
                sendMessageService.sendMessage(new Data(user.getChatId(), message, board, time, dayOfWeek,maxPlayer, true));
            } else {
                if(user.isActive()) {
                    board = builder().add("Записаться", callback).create();
                    sendMessageService.sendMessage(new Data(user.getChatId(), message, board, time, dayOfWeek, maxPlayer, false));
                }
            }
        });
    }

}
