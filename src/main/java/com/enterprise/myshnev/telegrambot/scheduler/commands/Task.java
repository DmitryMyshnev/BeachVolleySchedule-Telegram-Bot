package com.enterprise.myshnev.telegrambot.scheduler.commands;


import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;

import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.Data;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.Symbols.*;

import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.ENJOY;

public class Task extends TimerTask {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private int maxPlayer;
    private String message;
    private InlineKeyboardMarkup board;
    private Data data;


    public Task(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        data = new Data();
    }

    @Override
    public void run() {
        createNotification("ПТ");
    }

    private void createNotification(String weekOfDay) {

        List<Workouts> workoutsList = workoutService.findBy("Workout", "week_of_day", weekOfDay, new WorkoutsTable()).stream().map(w -> (Workouts) w).collect(Collectors.toList());
        if (workoutsList.size() != 0) {
            maxPlayer = workoutsList.get(0).getMaxCountUser();
        }
        workoutsList.stream().forEach(workouts -> {
            String time = workouts.getTime();
            String callback = ENJOY.getCommandName() + "/" + weekOfDay + "/" + time +"/" + maxPlayer;
            board = builder().add("Записаться", callback).create();
            message = "Запись на тренировку в " + weekOfDay + " в " + time + " открыта!\nКоличество свободных мест:    " + getSymbol(maxPlayer);
            workoutService.addTable(time,new NewWorkoutTable());
            userService.findAll("Users",new UserTable()).stream().map(u -> (TelegramUser) u).collect(Collectors.toList()).forEach(user -> {
                if(user.isCoach()){
                    sendMessageService.sendMessage(new Data(user.getChatId(),message,time));
                }else
                sendMessageService.sendMessage(new Data(user.getChatId(), message, board,weekOfDay,time,false));
            });
        });
    }
}
