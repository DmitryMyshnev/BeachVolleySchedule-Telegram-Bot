package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.StatisticTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Statistic;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.Data;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.Symbols.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.STATISTIC;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.USERS;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;


import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.repository.entity.UserAction.*;
import static com.enterprise.myshnev.telegrambot.scheduler.repository.entity.UserAction.ADD;

public class AddUserToWorkoutButton implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private static final Long ONE_DAY = 86400000L;
    private String timeOfWorkout;
    private String dayOfWeek;
    private Integer messageId;
    private int freePlaces;
    private String message;
    private Integer maxSize;
    private final Statistic stat;
    private final StatisticTable statisticTable;
    private final NewWorkoutTable newWorkoutTable;
    private InlineKeyboardMarkup board;
    private String callback;
    private String workoutTableName;
    private final SimpleDateFormat Date;
    private final SimpleDateFormat formatOfDay;
    private final SimpleDateFormat formatOfWeek;

    public AddUserToWorkoutButton(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        newWorkoutTable = new NewWorkoutTable();
        statisticTable = new StatisticTable();
        Date = new SimpleDateFormat("d.MM.yy");
        stat = new Statistic();
        Locale locale = new Locale("ru");
        formatOfDay  = new SimpleDateFormat("E d.MMM",locale);
        formatOfWeek = new SimpleDateFormat("E",locale);
    }

    @Override
    public void execute(Update update) {
        String date;
        dayOfWeek = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        if(dayOfWeek.equals(formatOfWeek.format(System.currentTimeMillis()))) {
            date = formatOfDay.format(System.currentTimeMillis());
        }else {
            date =  formatOfDay.format(System.currentTimeMillis()+ ONE_DAY);
        }
        timeOfWorkout = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        maxSize = Integer.parseInt(Objects.requireNonNull(getCallbackQuery(update)).split("/")[3]);
        workoutTableName = dayOfWeek + timeOfWorkout;
        AtomicInteger number = new AtomicInteger(0);
        freePlaces = maxSize - workoutService.count(workoutTableName, newWorkoutTable);
        message = ("Запись на тренировку в " + date + " в " + timeOfWorkout + " открыта!\nКоличество свободных мест:   %s \nСписок записавшихся: \n");
        callback = ENJOY.getCommandName() + "/" + dayOfWeek + "/" + timeOfWorkout + "/" + maxSize;
        workoutService.findByChatId(workoutTableName, getChatId(update), newWorkoutTable).map(m -> (NewWorkout) m).ifPresentOrElse(
                workout -> {
                    workoutService.delete(workoutTableName, getChatId(update), newWorkoutTable);
                   addStatistic(update, REMOVE.getUserAction());
                    freePlaces++;
                    if (!workout.isReserve()) {
                        if (freePlaces <= 0) {
                            workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(w -> (NewWorkout) w).filter(NewWorkout::isReserve)
                                    .findFirst()
                                    .ifPresent(p -> {
                                        workoutService.update(newWorkoutTable, workoutTableName, p.getUserId(), "reserve", "0");
                                        String notification = "\uD83E\uDD73  Место освободилось! Ждем на тренировке.";
                                        sendMessageService.sendMessage(p.getUserId(), notification, null);
                                    });
                            workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(r -> !r.isReserve()).forEach(user -> {
                                message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                            });
                        } else {
                            workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(r -> !r.isReserve()).forEach(user -> {
                                message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                            });
                        }
                    } else {
                        workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(r -> !r.isReserve()).forEach(user -> {
                            message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                        });
                        if (freePlaces < 0) {
                            message += "❗️Резерв: \n";
                            number.set(0);
                            workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(NewWorkout::isReserve).forEach(user -> {
                                message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                            });
                        }
                    }
                },
                () -> {
                    boolean isReserve;
                    isReserve = freePlaces <= 0;
                    workoutService.save(workoutTableName, createEntity(update, isReserve), newWorkoutTable);
                    freePlaces--;
                    if (isReserve) {
                        workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(u -> (!u.isReserve())).forEach(user -> {
                            message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                        });
                        message += "❗️Резерв: \n";
                        number.set(0);
                        workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).filter(NewWorkout::isReserve).forEach(user -> {
                            message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                        });
                    } else {
                        workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(m -> (NewWorkout) m).forEach(user -> {
                            message += (number.incrementAndGet() + ". " + user.getFirstName() + " " + user.getLastName() + "\n");
                        });
                    }

                });
        sendMessageForAllUsers();
    }

    private void sendMessageForAllUsers() {
        userService.findAll(USERS.getTableName(), new UserTable()).stream().map(u -> (TelegramUser) u).collect(Collectors.toList()).forEach(user -> {
            if(!user.isActive()) {
                if (sendMessageService.getData(user.getChatId()).isEmpty()) {
                    String text = freePlaces <= 0 ? "Записаться в резерв" : "Записаться ";
                    board = builder().add(text, callback).create();
                    sendMessageService.sendMessage(new Data(user.getChatId(), String.format(message, getSymbol(freePlaces)), board, timeOfWorkout, dayOfWeek, maxSize, false));
                } else {
                    sendMessageService.getData(user.getChatId()).forEach(data -> {
                        if (timeOfWorkout.equals(data.getTimeWorkout()))
                            messageId = data.getMessageId();
                    });
                    if (user.isCoach()) {
                        board = builder().add("Отменить тренировку", "cancel_workout/" + dayOfWeek + "/" + timeOfWorkout).create();
                    } else {
                        if (workoutService.findByChatId(workoutTableName, user.getChatId(), newWorkoutTable).isPresent()) {
                            board = builder().add("Охрана, отмена!", callback).create();
                        } else {
                            String text = freePlaces <= 0 ? "Записаться в резерв" : "Записаться ";
                            board = builder().add(text, callback).create();
                        }
                    }
                    sendMessageService.getData(user.getChatId()).stream()
                            .filter(f -> (f.getDayOfWeek().equals(dayOfWeek) && f.getTimeWorkout().equals(timeOfWorkout)))
                            .findFirst().ifPresent(p -> {
                        p.setMessage(message);
                    });

                    sendMessageService.editMessage(user.getChatId(), messageId, String.format(message, getSymbol(freePlaces)), board);
                }
            }
        });
    }

    private NewWorkout createEntity(Update update, boolean reserve) {
        NewWorkout workout = new NewWorkout();
        workout.setUserId(getChatId(update));
        workout.setFirstName(getFirstName(update));
        if (getLastName(update) != null) {
            workout.setLastName(getLastName(update));
        } else {
            workout.setLastName("");
        }
        workout.setReserve(reserve);
        if(reserve)
            addStatistic(update, ADD_TO_RESERVE.getUserAction());
        else
            addStatistic(update, ADD.getUserAction());
        return workout;
    }
    private void addStatistic(Update update,String action){
        stat.setUserId(getChatId(update));
        stat.setUserName(getFirstName(update)+" "+getLastName(update));
        stat.setWorkout(dayOfWeek+" "+ timeOfWorkout);
        stat.setAction(action);
        stat.setDate(Date.format(System.currentTimeMillis()));
        userService.save(STATISTIC.getTableName(),stat, statisticTable);
    }
}
