package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.SimpleDateFormat;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;
import static java.util.concurrent.TimeUnit.*;

public class ConfirmCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private String message;
    private Integer messageId;
    private String timeOfWorkout;
    private String weekOfDay;
    private final NewWorkoutTable newWorkoutTable;
    private final SimpleDateFormat formatOfDay;
    private final UserTable userTable;
    private final WorkoutsTable workoutsTable;

    public ConfirmCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        newWorkoutTable = new NewWorkoutTable();
        formatOfDay = new SimpleDateFormat("E d MMM",new Locale("ru"));
        userTable = new UserTable();
        workoutsTable = new WorkoutsTable();
    }

    @Override
    public void execute(Update update) {
        String answer = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        weekOfDay = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        timeOfWorkout = Objects.requireNonNull(getCallbackQuery(update)).split("/")[3];
        String tableName = weekOfDay + timeOfWorkout;
        if (answer.equals("yes")) {
            workoutService.dropTable(tableName, newWorkoutTable);
            workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream()
                    .map(m -> (Workouts) m)
                    .filter(f -> (f.getDayOfWeek().equals(weekOfDay) && f.getTime().equals(timeOfWorkout))).findFirst().ifPresent(w -> {
                workoutService.update(workoutsTable, WORKOUT.getTableName(), w.getId().toString(), "active", "0");
            });
            sendMessageForAllUsers();
        } else {
            AtomicInteger maxSize = new AtomicInteger(0);
            sendMessageService.getData(getChatId(update)).stream()
                    .filter(f -> (f.getTimeWorkout().equals(timeOfWorkout) && f.getDayOfWeek().equals(weekOfDay))).findFirst()
                    .ifPresent(p -> {
                        Long count = workoutService.findAll(weekOfDay + timeOfWorkout, newWorkoutTable).stream().map(m -> (NewWorkout) m)
                                .filter(f -> (!f.isReserve())).count();
                        workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream().map(m -> (Workouts) m)
                                .filter(wts -> (wts.getDayOfWeek().equals(weekOfDay) && wts.getTime().equals(timeOfWorkout))).findFirst()
                                .ifPresent(ps -> maxSize.set(ps.getMaxCountUser()));
                        Long freePlaces = maxSize.get() - count;
                        sendMessageService.editMessage(getChatId(update), p.getMessageId(), String.format(p.getMessage(), Symbols.getSymbol(freePlaces.intValue())), p.getKeyBoard());
                    });
            sendMessageService.deleteWorkoutMessage(getChatId(update), getMessageId(update));
        }
        TelegramBot.getInstance().filterQuery.remove(getChatId(update));
    }

    private void sendMessageForAllUsers() {
        String date = formatOfDay.format(System.currentTimeMillis() + DAYS.toMillis(1));
        message = "❌ Тренировка  " + date + " в " + timeOfWorkout + " отменена!";
        userService.findAll(USERS.getTableName(), userTable).stream().map(u -> (TelegramUser) u).collect(Collectors.toList()).forEach(user -> {
            if (user.isCoach()) {
                sendMessageService.getData(user.getChatId()).stream()
                        .filter(f -> (f.getTimeWorkout().equals(timeOfWorkout))).collect(Collectors.toList())
                        .forEach(p -> {
                            sendMessageService.deleteWorkoutMessage(user.getChatId(), p.getMessageId());
                        });
                sendMessageService.sendMessage(user.getChatId(), message, null);

            } else {
                    sendMessageService.getData(user.getChatId()).stream()
                            .filter(f -> (f.getTimeWorkout().equals(timeOfWorkout)))
                            .findFirst().ifPresent(w -> {
                        sendMessageService.deleteWorkoutMessage(user.getChatId(), w.getMessageId());
                        sendMessageService.sendMessage(user.getChatId(), message, null);
                    });
            }
        });
    }
}
