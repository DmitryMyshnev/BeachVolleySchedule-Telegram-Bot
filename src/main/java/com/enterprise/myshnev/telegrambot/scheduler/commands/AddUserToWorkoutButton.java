package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.Symbols.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class AddUserToWorkoutButton implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private String timeOfWorkout;
    private Integer messageId;
    private int freePlaces;
    private String message;

    private final NewWorkoutTable newWorkoutTable;
    private InlineKeyboardMarkup board;
    private String callback;
    private String tableName;

    public AddUserToWorkoutButton(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        newWorkoutTable = new NewWorkoutTable();
    }

    @Override
    public void execute(Update update) {
        String weekOfDay = getCallbackQuery(update).split("/")[1];
        timeOfWorkout = getCallbackQuery(update).split("/")[2];
        Integer maxSize = Integer.parseInt(getCallbackQuery(update).split("/")[3]);
        tableName = timeOfWorkout;
        AtomicInteger number = new AtomicInteger(0);
        freePlaces = maxSize - workoutService.count(tableName, newWorkoutTable);
        message = ("Запись на тренировку в " + weekOfDay + " в " + timeOfWorkout + " открыта!\nКоличество свободных мест:   %s \nСписок записавшихся: \n");
        callback = ENJOY.getCommandName() + "/" + weekOfDay + "/" + timeOfWorkout + "/" + maxSize;
        workoutService.findByChatId(tableName, getChatId(update), newWorkoutTable).map(m -> (NewWorkout) m).ifPresentOrElse(
                workout -> {
                    workoutService.delete(tableName, getChatId(update), newWorkoutTable);
                    freePlaces++;
                    if (!workout.isReserve()) {
                        if (freePlaces <= 0) {
                            workoutService.findAll(tableName, newWorkoutTable).stream().map(w -> (NewWorkout) w).filter(NewWorkout::isReserve)
                                    .findFirst()
                                    .ifPresent(p -> {
                                        workoutService.update(newWorkoutTable, tableName, p.getUserId(), "reserve", "0");
                                        String notification = "\uD83E\uDD73  Место освободилось! Ждем на тренировке.";
                                        sendMessageService.sendMessage(p.getUserId(), notification);
                                    });
                        } else {
                            workoutService.findAll(tableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(r -> !r.isReserve()).forEach(user -> {
                                message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                            });
                        }
                    } else {
                        workoutService.findAll(tableName,newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(r -> !r.isReserve()).forEach(user -> {
                            message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                        });
                        if (freePlaces < 0) {
                            message += "❗️Резерв: \n";
                            number.set(0);
                            workoutService.findAll(tableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(NewWorkout::isReserve).forEach(user -> {
                                message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                            });
                        }
                    }
                },
                () -> {
                    boolean reserve;
                    reserve = freePlaces <= 0;
                    workoutService.save(tableName, createEntity(update, reserve), newWorkoutTable);
                    freePlaces--;
                    if (reserve) {
                        workoutService.findAll(tableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(u -> (!u.isReserve())).forEach(user -> {
                            message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                        });
                        message += "❗️Резерв: \n";
                        number.set(0);
                        workoutService.findAll(tableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(NewWorkout::isReserve).forEach(user -> {
                            message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                        });
                    } else {
                        workoutService.findAll(tableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).forEach(user -> {
                            message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                        });
                    }
                });
        sendMessageForAllUsers();
    }

    private void sendMessageForAllUsers() {
        userService.findAll("Users", new UserTable()).stream().map(u -> (TelegramUser) u).collect(Collectors.toList()).forEach(user -> {
            sendMessageService.getData(user.getChatId()).forEach(data -> {
                if (timeOfWorkout.equals(data.getTimeWorkout()))
                    messageId = data.getMessageId();
            });
            if (workoutService.findByChatId(tableName, user.getChatId(), newWorkoutTable).isPresent()) {
                board = builder().add("Охрана, отмена!", callback).create();
            } else {
                String text;
                text = freePlaces <= 0 ? "Записаться в резерв" : "Записаться ";
                board = builder().add(text, callback).create();
            }
            if (user.isCoach()) {
                sendMessageService.editMessage(user.getChatId(), messageId, String.format(message, getSymbol(freePlaces)));
            } else {
                sendMessageService.editMessage(user.getChatId(), messageId, String.format(message, getSymbol(freePlaces)), board);
            }
        });
    }

    private NewWorkout createEntity(Update update, boolean reserve) {
        NewWorkout workout = new NewWorkout();
        workout.setUserId(getChatId(update));
        workout.setFirstName(getFirstName(update));
        workout.setLastName(getLastName(update));
        workout.setReserve(reserve);
        return workout;
    }
}
