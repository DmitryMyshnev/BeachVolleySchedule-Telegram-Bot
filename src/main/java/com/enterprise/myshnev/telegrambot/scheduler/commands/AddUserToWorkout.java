package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.*;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.*;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.STATISTIC;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.repository.entity.UserAction.*;
import static com.enterprise.myshnev.telegrambot.scheduler.repository.entity.UserAction.ADD;

public class AddUserToWorkout implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private static final Long ONE_DAY = 86400000L;
    private String timeOfWorkout;
    private String dayOfWeek;
    private int freePlaces;
    private String message;
    private Integer maxSize;
    private final Statistic stat;
    private final StatisticTable statisticTable;
    private final NewWorkoutTable newWorkoutTable;
    private final WorkoutsTable workoutTable;
    private final UserTable userTable;
    private final MessageIdTable messageIdTable;
    private InlineKeyboardMarkup board;
    private String callback;
    private String workoutTableName;
    private final SimpleDateFormat Date;
    private final SimpleDateFormat formatOfDay;
    private final SimpleDateFormat formatOfWeek;
    private List<NewWorkout> workoutList;
    private String coachChatId;

    public AddUserToWorkout(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        userTable = new UserTable();
        newWorkoutTable = new NewWorkoutTable();
        workoutTable = new WorkoutsTable();
        statisticTable = new StatisticTable();
        messageIdTable = new MessageIdTable();
        Date = new SimpleDateFormat("dd.MM.yy H:mm:ss");
        stat = new Statistic();
        Locale locale = new Locale("ru");
        formatOfDay = new SimpleDateFormat("E d.MMM", locale);
        formatOfWeek = new SimpleDateFormat("E", locale);
        workoutList = new ArrayList<>();
    }

    @Override
    public void execute(Update update) {
        dayOfWeek = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        timeOfWorkout = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        maxSize = Integer.parseInt(Objects.requireNonNull(getCallbackQuery(update)).split("/")[3]);
        workoutTableName = dayOfWeek + timeOfWorkout;
        userService.findAll(USERS.getTableName(), userTable).stream().map(TelegramUser.class::cast).filter(TelegramUser::isCoach).findFirst().ifPresent(coach -> {
            coachChatId = coach.getChatId();
        });
        String command = Objects.requireNonNull(getCallbackQuery(update)).split("/")[4];

        if (!TelegramBot.getInstance().notifyMessageId.isEmpty()) {
            Message msg = Objects.requireNonNull(TelegramBot.getInstance().notifyMessageId.poll());
            boolean complete = sendMessageService.deleteMessage(getChatId(update), msg.getMessageId());
            if (!complete) {
                sendMessageService.deleteMessage(msg.getChatId().toString(), msg.getMessageId());
            }
        }
        if (!validWorkout(workoutTableName,getMessageId(update))) {
            return;
        }
        long countNotReserve = workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(NewWorkout.class::cast)
                .filter(f -> (!f.isReserve())).count();
        freePlaces = maxSize - (int) countNotReserve;
        workoutService.findByChatId(workoutTableName, getChatId(update), newWorkoutTable).map(NewWorkout.class::cast).ifPresentOrElse(
                people -> {
                    if (command.equals("cancel")) {
                        workoutService.delete(workoutTableName, people.getUserId(), newWorkoutTable);
                        addStatistic(update, REMOVE.getUserAction());
                        if (!people.isReserve() && freePlaces >= 0) {
                            freePlaces++;
                            workoutService.findAll(workoutTableName, newWorkoutTable).stream().map(w -> (NewWorkout) w)
                                    .filter(NewWorkout::isReserve)
                                    .findFirst()
                                    .ifPresent(p -> {
                                        workoutService.update(newWorkoutTable, workoutTableName, p.getUserId(), "reserve", "0");
                                        freePlaces--;
                                        String notification = "\uD83E\uDD73  Место освободилось! Ждем на тренировке в " + dayOfWeek + " в " + timeOfWorkout;
                                        sendMessageService.sendMessage(p.getUserId(), notification, null);
                                    });
                        }
                        callback = ENJOY.getCommandName() + "/" + dayOfWeek + "/" + timeOfWorkout + "/" + maxSize + "/join";
                        String text = freePlaces <= 0 ? "Записаться в резерв" : "Записаться";
                        board = builder().add(text, callback).create();

                    }
                },
                () -> {
                    boolean isReserve;
                    if (command.equals("join")) {
                        isReserve = workoutService.findAll(workoutTableName, newWorkoutTable).stream()
                                .map(NewWorkout.class::cast).filter(f -> (!f.isReserve())).count() >= maxSize;
                        workoutService.save(workoutTableName, createEntity(update, isReserve), newWorkoutTable);
                        if (!isReserve) {
                            addStatistic(update, ADD.getUserAction());
                            freePlaces--;
                        } else {
                            addStatistic(update, ADD_TO_RESERVE.getUserAction());
                        }
                        callback = ENJOY.getCommandName() + "/" + dayOfWeek + "/" + timeOfWorkout + "/" + maxSize + "/cancel";
                        board = builder().add("Отмена", callback).create();

                    }
                });
        workoutList = workoutService.findAll(workoutTableName, newWorkoutTable).stream()
                .map(NewWorkout.class::cast).collect(Collectors.toList());

        message = createListUsers(workoutList, freePlaces, getChatId(update));
        sendMessageService.editMessage(getChatId(update), getMessageId(update), message, board);


        editMessageForAllUsers(workoutList, getChatId(update));
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

    private void editMessageForAllUsers(List<NewWorkout> users, String chatId) {
        AtomicReference<InlineKeyboardMarkup> board = new AtomicReference<>();
        AtomicReference<String> msg = new AtomicReference<>();
        userService.findAll(MESSAGE_ID.getTableName(), messageIdTable).stream().map(MessageId.class::cast).forEach(user -> {

            msg.set(createListUsers(users, freePlaces, user.getChatId()));

            if (user.getChatId().equals(coachChatId)) {
                board.set(builder().add("Отменить тренировку", "cancel_workout/" + dayOfWeek + "/" + timeOfWorkout).create());
                sendMessageService.editMessage(user.getChatId(), user.getMessageId(), msg.get(), board.get());
            } else {
                if (!user.getChatId().equals(chatId)) {
                    workoutService.findByChatId(workoutTableName, user.getChatId(), newWorkoutTable).ifPresentOrElse(p -> {
                        board.set(builder().add("Отмена", ENJOY.getCommandName() + "/" + dayOfWeek + "/" + timeOfWorkout + "/" + maxSize + "/cancel").create());
                        sendMessageService.editMessage(user.getChatId(), user.getMessageId(), msg.get(), board.get());
                    }, () -> {
                        String text = freePlaces <= 0 ? "Записаться в резерв" : "Записаться";
                        board.set(builder().add(text, ENJOY.getCommandName() + "/" + dayOfWeek + "/" + timeOfWorkout + "/" + maxSize + "/join").create());
                        sendMessageService.editMessage(user.getChatId(), user.getMessageId(), msg.get(), board.get());
                    });
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
        return workout;
    }

    private void addStatistic(Update update, String action) {
        stat.setUserId(getChatId(update));
        String lastName = getLastName(update) == null ? "" : getLastName(update);
        stat.setUserName(getFirstName(update) + " " + lastName);
        stat.setWorkout(dayOfWeek + " " + timeOfWorkout);
        stat.setAction(action);
        stat.setDate(Date.format(System.currentTimeMillis()));
        userService.save(STATISTIC.getTableName(), stat, statisticTable);
    }

    private boolean validWorkout(String nameOfWorkout, Integer messageId) {
        AtomicBoolean isValid = new AtomicBoolean(false);
        workoutService.findAll(WORKOUT.getTableName(), workoutTable).stream().map(m -> (Workouts) m)
                .forEach(w -> {
                    if ((w.getDayOfWeek() + w.getTime()).equals(nameOfWorkout) && w.isActive()) {
                        if (userService.findByChatId(MESSAGE_ID.getTableName(), messageId.toString(), messageIdTable).isPresent())
                            isValid.set(true);
                    }
                });
        return isValid.get();
    }
}
