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
    private static final Long ONE_DAY = 86400000L;
    private final CoachTable coachTable;
    private final WorkoutsTable workoutsTable;
    private final NewWorkoutTable newWorkoutTable;
    private final AdminTable adminTable;
    private final UserTable userTable;
    private StringBuilder nameCoach = new StringBuilder();
    private final SimpleDateFormat formatOfDay;
    private final SimpleDateFormat formatOfWeek;

    public StartCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        coachTable = new CoachTable();
        workoutsTable = new WorkoutsTable();
        newWorkoutTable = new NewWorkoutTable();
        userTable = new UserTable();
        adminTable = new AdminTable();
        formatOfDay = new SimpleDateFormat("E d.MMM", new Locale("ru"));
        formatOfWeek = new SimpleDateFormat("E", new Locale("ru"));
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(COACH.getTableName(), getChatId(update), coachTable).map(TelegramUser.class::cast).ifPresentOrElse(coach -> {
            message = "Привет, " + coach.getFirstName() + "! ";
            sendMessageService.sendMessage(coach.getChatId(), message, null);
        }, () -> {
            userService.findByChatId(ADMIN.getTableName(), getChatId(update), adminTable).ifPresentOrElse(admin -> {
                sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
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
                            "/workouts - посмотреть рассписание тренировок\n" +
                            "/stop - отключить уведомления\n" +
                            "/start - включить уведомления\n";
                    sendMessageService.sendMessage(getChatId(update), message, null);
                    findActiveWorkout(getChatId(update));
                } else {
                    userService.findByChatId(USERS.getTableName(), getChatId(update), userTable).map(TelegramUser.class::cast)
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
        });
    }

    private void findActiveWorkout(String chatId) {
        StringBuilder buttonText = new StringBuilder();
        StringBuilder mess = new StringBuilder();
        workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream()
                .map(m -> (Workouts) m)
                .filter(Workouts::isActive)
                .forEach(w -> {
                    timeOfWorkout = w.getTime();
                    dayOfWeek = w.getDayOfWeek();
                    callback = ENJOY.getCommandName() + "/" + w.getDayOfWeek() + "/" + timeOfWorkout + "/" + w.getMaxCountUser() + "/join";
                    List<NewWorkout> signedUpUsers = workoutService.findAll(w.getDayOfWeek() + w.getTime(), newWorkoutTable).stream()
                            .map(NewWorkout.class::cast).collect(Collectors.toList());
                    long freePlaces = w.getMaxCountUser() - signedUpUsers.stream().filter(r -> !r.isReserve()).count();
                    mess.append(createListUsers(signedUpUsers,(int) freePlaces,chatId));
                    buttonText.append(freePlaces <=0 ? "Записаться в резерв":"Записаться");
                    board = builder().add(buttonText.toString(), callback).create();
                    sendMessageService.sendMessage(new Data(chatId, mess.toString(), board, timeOfWorkout, dayOfWeek, w.getMaxCountUser(), false));
                    buttonText.delete(0, buttonText.length());
                });
    }
    private String createListUsers(List<NewWorkout> users, int places, String chatId) {
        AtomicInteger number = new AtomicInteger(0);
        String date;
        if (dayOfWeek.equals(formatOfWeek.format(System.currentTimeMillis()))) {
            date = formatOfDay.format(System.currentTimeMillis());
        } else {
            date = formatOfDay.format(System.currentTimeMillis() + ONE_DAY);
        }
        StringBuilder message = new StringBuilder("Запись на тренировку в " + date + " в " + timeOfWorkout + " открыта!\n "
                + "Количество свободных мест:   %s \nСписок записавшихся: \n");

        users.stream().filter(f -> (!f.isReserve()))
                .forEach(u -> {
                    if (u.getUserId().equals(chatId)) {
                        message.append(number.incrementAndGet()).append(". ").append("<i>Я</i>\n");
                    } else
                        message.append(number.incrementAndGet()).append(". ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
                });
        number.set(0);
        List<NewWorkout> reserve = users.stream().filter(NewWorkout::isReserve).toList();
        if (!reserve.isEmpty()) {
            message.append("<strong>Резерв:</strong>\n");
            reserve.forEach(u -> {
                if (u.getUserId().equals(chatId)) {
                    message.append(number.incrementAndGet()).append(". ").append("<i>Я</i>\n");
                } else
                    message.append(number.incrementAndGet()).append(". ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
            });
        }
        return String.format(message.toString(), Symbols.getSymbol(Math.max(places, 0)));
    }
}
