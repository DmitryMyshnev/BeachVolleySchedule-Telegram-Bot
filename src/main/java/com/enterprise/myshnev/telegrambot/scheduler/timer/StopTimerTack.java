package com.enterprise.myshnev.telegrambot.scheduler.timer;


import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.MessageIdTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.MessageId;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Optional;
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
    private final SimpleDateFormat date;

    public StopTimerTack(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        newWorkoutTable = new NewWorkoutTable();
        userTable = new UserTable();
        messageIdTable = new MessageIdTable();
        workoutsTable = new WorkoutsTable();
        date = new SimpleDateFormat("E d MMM", new Locale("ru"));
    }

    public void breakWorkout(String weekOfDay, String timeWorkout, Integer id) {
        AtomicBoolean isActive = new AtomicBoolean(false);
        workoutService.findByChatId(WORKOUT.getTableName(), id.toString(), workoutsTable).map(Workouts.class::cast)
                .ifPresent(p -> isActive.set(p.isActive()));
        if (isActive.get()) {
            message = "Запись на тренировку в " + date.format(System.currentTimeMillis()) +
                    " в " + timeWorkout +
                    " закрыта!\nСостав группы:\n" + getAllUserWhoGo(weekOfDay + timeWorkout);
            userService.findAll(USERS.getTableName(), userTable).stream()
                    .map(TelegramUser.class::cast).filter(TelegramUser::isActive)
                    .forEach(user -> {
                        Optional<MessageId> messageId = userService.findAll(MESSAGE_ID.getTableName(), messageIdTable).stream().map(MessageId.class::cast)
                                .filter(f -> f.getChatId().equals(user.getChatId()) && f.getTime().equals(timeWorkout) && f.getDayOfWeek().equals(weekOfDay))
                                .findFirst();
                        messageId.ifPresent(value -> workoutService.findByChatId(weekOfDay + timeWorkout, value.getChatId(), newWorkoutTable).stream()
                                .map(NewWorkout.class::cast).findFirst()
                                .ifPresentOrElse(p -> {
                                    if (!p.isReserve()) {
                                        sendMessageService.editMessage(user.getChatId(), value.getMessageId(), message, null);
                                        // sendMessageService.deleteMessageId(user.getChatId(), messageId.get().getMessageId());
                                        sendMessageService.sendMessage(user.getChatId(), "❗ Напоминание.\n " + "В " + timeWorkout +
                                                " у Вас тренировка!\n ", null);
                                    } else {
                                        sendMessageService.deleteWorkoutMessage(user.getChatId(), value.getMessageId());
                                    }
                                    userService.delete(MESSAGE_ID.getTableName(), value.getMessageId().toString(),messageIdTable);

                                }, () -> {
                                    if (!user.isCoach()) {
                                        sendMessageService.deleteWorkoutMessage(user.getChatId(), value.getMessageId());
                                    } else {
                                        sendMessageService.editMessage(user.getChatId(), value.getMessageId(), message, null);
                                    }
                                    userService.delete(MESSAGE_ID.getTableName(), value.getMessageId().toString(), messageIdTable);
                                }));
                    });
            workoutService.update(workoutsTable, WORKOUT.getTableName(), id.toString(), "active", "0");
            workoutService.dropTable(weekOfDay + timeWorkout, newWorkoutTable);


            TelegramBot.getInstance().notifyMessageId.clear();
        }
    }


    private String getAllUserWhoGo(String tableName) {
        StringBuilder list = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);
        workoutService.findAll(tableName, newWorkoutTable).stream()
                .map(NewWorkout.class::cast)
                .filter(f -> (!f.isReserve()))
                .forEach(user -> {
                    list.append(count.addAndGet(1)).append(". ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
                });
        return list.toString();
    }
}
