package com.enterprise.myshnev.telegrambot.scheduler.timer;

import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import  static java.time.LocalTime.now;
import java.time.LocalTime;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

public class TimerOfNotification {
    private final Timer timer;
    private final Task task;

    public TimerOfNotification(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.timer =new Timer();
        this.task = new Task(sendMessageService,userService,workoutService);
    }
    public void startTimer(){
        long delay;
        if(now().getSecond() == 0) {
            delay = 0;
        }else {
            delay = MINUTES.toMillis(1) - SECONDS.toMillis(now().getSecond());
        }
        timer.schedule(task, delay,MINUTES.toMillis(1));
     // timer.schedule(task, 1000);
    }
}
