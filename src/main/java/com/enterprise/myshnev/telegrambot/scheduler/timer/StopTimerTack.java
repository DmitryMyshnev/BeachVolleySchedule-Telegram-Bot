package com.enterprise.myshnev.telegrambot.scheduler.timer;


import com.enterprise.myshnev.telegrambot.scheduler.db.table.MessageIdTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;

public class StopTimerTack {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private final NewWorkoutTable newWorkoutTable;
    private final UserTable userTable;
    private final MessageIdTable messageIdTable;
    private final WorkoutsTable workoutsTable;
    private String message;
    private SimpleDateFormat date ;
    public StopTimerTack(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        newWorkoutTable = new NewWorkoutTable();
        userTable = new UserTable();
        messageIdTable = new MessageIdTable();
        workoutsTable = new WorkoutsTable();
        date = new SimpleDateFormat("E d MMM",new Locale("ru"));
    }

    public void breakWorkout(String weekOfDay, String timeWorkout, Integer id) {
        AtomicBoolean isActive = new AtomicBoolean(false);
        AtomicBoolean isDelete = new AtomicBoolean(false);
        workoutService.findByChatId(WORKOUT.getTableName(), id.toString(), workoutsTable).map(m -> (Workouts) m).ifPresent(p -> isActive.set(p.isActive()));
        if (isActive.get()) {
            userService.findAll(USERS.getTableName(), userTable).stream()
                    .map(m -> (TelegramUser) m)
                    .forEach(user -> sendMessageService.getData(user.getChatId()).stream()
                            .filter(f -> f.getTimeWorkout().equals(timeWorkout) && f.getDayOfWeek().equals(weekOfDay))
                            .findFirst()
                            .ifPresent(d -> {
                                if (!user.isCoach()) {
                                    isDelete.set(sendMessageService.deleteWorkoutMessage(user.getChatId(), d.getMessageId()));
                                } else {

                                    message = "Запись на тренировку в " + date.format(System.currentTimeMillis()) +
                                            " в " + timeWorkout +
                                            " закрыта!\nСостав группы:\n" + getAllUserWhoGo(weekOfDay + timeWorkout);
                                    sendMessageService.editMessage(user.getChatId(), d.getMessageId(), message, null);
                                    if (isDelete.get())
                                        sendMessageService.deleteMessageId(user.getChatId(), d.getMessageId());
                                }
                            }));
            if(isDelete.get()) {
                workoutService.update(workoutsTable, WORKOUT.getTableName(), id.toString(), "active", "0");
                workoutService.dropTable(weekOfDay + timeWorkout, newWorkoutTable);
            }
        }
    }

    private String getAllUserWhoGo(String tableName) {
        StringBuilder list = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);
        workoutService.findAll(tableName, newWorkoutTable).stream()
                .map(m -> (NewWorkout) m)
                .filter(f -> (!f.isReserve()))
                .forEach(user -> {
                    list.append(count.addAndGet(1)).append(". ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
                });
        return list.toString();
    }
}
