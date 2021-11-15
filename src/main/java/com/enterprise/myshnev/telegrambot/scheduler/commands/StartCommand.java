package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.*;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.ENJOY;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.Symbols.getSymbol;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;


import static java.util.concurrent.TimeUnit.*;

import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.Data;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;

public class StartCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private String message;
    private InlineKeyboardMarkup board;
    private String callback;
    private String timeOfWorkout;
    private String dayOfWeek;
    private final CoachTable coachTable;
    private final WorkoutsTable workoutsTable;
    private final NewWorkoutTable newWorkoutTable;
    private final UserTable userTable;
    private StringBuilder nameCoach = new StringBuilder();

    public StartCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        coachTable = new CoachTable();
        workoutsTable = new WorkoutsTable();
        newWorkoutTable = new NewWorkoutTable();
        userTable = new UserTable();
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(COACH.getTableName(), getChatId(update), coachTable).map(TelegramUser.class::cast).ifPresentOrElse(coach -> {
            message = "Привет, " + coach.getFirstName() + "! ";
            sendMessageService.sendMessage(coach.getChatId(), message, null);
        }, () -> {
            TelegramUser user = new TelegramUser(getChatId(update), getFirstName(update), getLastName(update));
            String stat = userService.save(USERS.getTableName(), user, userTable);
            if (!stat.equals(EXIST.getStatus())) {
                List<TelegramUser> coach = userService.findAll("Coach", coachTable).stream()
                        .map(us -> (TelegramUser) us).collect(Collectors.toList());
                if (!coach.isEmpty()) {
                    nameCoach.append("к тренеру:\n ").append(coach.get(0).getFirstName()).append(" ").append(coach.get(0).getLastName());
                }
                message = "Привет, " + getFirstName(update) + "! Этот бот поможет тебе записываться на тренировки   " +
                       nameCoach.toString() +
                        "\n /help - посмотреть инструцию к боту\n" +
                        "/workouts - посмотреть рассписание тренировок";
                sendMessageService.sendMessage(getChatId(update), message, null);
                findActiveWorkout(getChatId(update));
            } else {
                userService.findByChatId(USERS.getTableName(), getChatId(update), userTable).map(m -> (TelegramUser) m)
                        .ifPresent(u -> {
                            if (u.isActive()) {
                                message = "Вы уже зарегистрированы";
                            } else {
                                userService.update(userTable, USERS.getTableName(), getChatId(update), "active", "1");
                                message = "Уведомления включены.";
                                findActiveWorkout(getChatId(update));
                            }
                        });
                sendMessageService.sendMessage(getChatId(update), message, null);
                message = "";
            }
        });
    }

    private void findActiveWorkout(String chatId) {
        String date = new SimpleDateFormat("E d.MMM", new Locale("ru")).format(System.currentTimeMillis() + DAYS.toMillis(1));
        StringBuilder buttonText = new StringBuilder();
        AtomicInteger number = new AtomicInteger(0);
        StringBuilder mess = new StringBuilder();
        workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream()
                .map(m -> (Workouts) m)
                .filter(Workouts::isActive)
                .forEach(w -> {
                    mess.append("Запись на тренировку в " + date + " в %s" + " открыта!\nКоличество свободных мест:   %s \nСписок записавшихся: \n");
                    timeOfWorkout = w.getTime();
                    dayOfWeek = w.getDayOfWeek();
                    callback = ENJOY.getCommandName() + "/" + w.getDayOfWeek() + "/" + timeOfWorkout + "/" + w.getMaxCountUser();
                    List<NewWorkout> workout = workoutService.findAll(w.getDayOfWeek() + w.getTime(), newWorkoutTable).stream()
                            .map(m -> (NewWorkout) m).collect(Collectors.toList());
                    Long freePlaces = w.getMaxCountUser() - workout.stream().filter(r -> !r.isReserve()).count();
                    mess.append(String.format(message, timeOfWorkout, getSymbol(freePlaces.intValue())));
                    workout.stream()
                            .filter(r -> !r.isReserve())
                            .forEach(user -> {
                                mess.append(number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                            });
                    workout.stream()
                            .filter(NewWorkout::isReserve)
                            .findFirst()
                            .ifPresentOrElse(p -> {
                                mess.append("❗️Резерв: \n");
                                buttonText.append("Записаться в резерв");
                            }, () -> buttonText.append("Записаться"));

                    workout.stream()
                            .filter(NewWorkout::isReserve).forEach(user -> {
                        mess.append(number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                    });
                    board = builder().add(buttonText.toString(), callback).create();
                    sendMessageService.sendMessage(new Data(chatId, mess.toString(), board, timeOfWorkout, dayOfWeek, w.getMaxCountUser(), false));
                    buttonText.delete(0, buttonText.length());
                });
    }
}
