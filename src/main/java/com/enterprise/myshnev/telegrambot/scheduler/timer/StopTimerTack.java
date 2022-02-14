package com.enterprise.myshnev.telegrambot.scheduler.timer;


import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StopTimerTack {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private String message;
    private final SimpleDateFormat date;

    public StopTimerTack(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        date = new SimpleDateFormat("E d MMM", new Locale("ru"));
    }

    public void breakWorkout(Workout workout) {
        AtomicBoolean isActive = new AtomicBoolean(false);
        workoutService.findWorkout(workout.getId())
                .ifPresent(p -> isActive.set(p.isActive()));
        if (isActive.get()) {
            message = "Запись на тренировку в " + date.format(System.currentTimeMillis()) +
                    " в " + workout.getTime() +
                    " закрыта!\nСостав группы:\n" + getAllUserWhoGo(workout.getId());
            userService.findByWorkoutId(workout.getId())
                    .forEach(sentMessages -> {
                        workoutService.findJoinedUser(sentMessages.getUser().getId(), workout.getId()).ifPresentOrElse(user -> {
                            if (!user.isReserve()) {
                                sendMessageService.editMessage(user.getChatId(), sentMessages.getMessageId(), message, null);
                                sendMessageService.sendMessage(user.getChatId(), "❗ Напоминание.\n " + "В " + workout.getTime() +
                                        " у Вас тренировка!\n ", null);
                            } else {
                                sendMessageService.deleteWorkoutMessage(user.getChatId(), sentMessages);
                            }
                            workoutService.delete(user);
                            userService.deleteSentMessage(sentMessages);
                        }, () -> {
                            if (sentMessages.getUser().isEqualsRole("COACH")) {
                                sendMessageService.editMessage(sentMessages.getUser().getId(), sentMessages.getMessageId(), message, null);
                                userService.deleteSentMessage(sentMessages);
                            } else
                                sendMessageService.deleteWorkoutMessage(sentMessages.getUser().getId(), sentMessages);
                        });
                    });

            workout.setActive(false);
            workoutService.updateWorkout(workout);

            TelegramBot.getInstance().notifyMessageId.clear();
        }
    }

    private String getAllUserWhoGo(Long workoutId) {
        StringBuilder list = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);
        workoutService.findAllJoinedUsers(workoutId).stream()
                .filter(f -> (!f.isReserve()))
                .forEach(user -> {
                    list.append(count.addAndGet(1)).append(". ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
                });
        return list.toString();
    }
}
