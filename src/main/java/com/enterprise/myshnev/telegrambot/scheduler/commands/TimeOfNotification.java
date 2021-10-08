package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;

import java.util.Timer;
public class TimeOfNotification {
    private Timer timer;
    private Task task;

    private static final long ONE_HOUR = 3600000L;

    public TimeOfNotification(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.timer =new  Timer();
        this.task = new Task(sendMessageService,userService,workoutService);
    }
    public void startTimer(){
        timer.schedule(task, 1000);
    }
}
