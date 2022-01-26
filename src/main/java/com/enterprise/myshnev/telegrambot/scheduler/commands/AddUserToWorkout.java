package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.model.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.model.SentMessages;
import com.enterprise.myshnev.telegrambot.scheduler.model.Statistic;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.ENJOY;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;
import static com.enterprise.myshnev.telegrambot.scheduler.model.UserAction.*;

public class AddUserToWorkout implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private static final Long ONE_DAY = 86400000L;
    private String timeOfWorkout;
    private String dayOfWeek;
    private int freePlaces;
    private Integer maxSize;
    private final Statistic stat;

    private InlineKeyboardMarkup board;
    private String callback;
    private final SimpleDateFormat Date;
    private final SimpleDateFormat formatOfDay;
    private final SimpleDateFormat formatOfWeek;
    private List<NewWorkout> workoutList;
    private String coachChatId;
    private Workout workout;

    public AddUserToWorkout(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        Date = new SimpleDateFormat("dd.MM.yy H:mm:ss");
        stat = new Statistic();
        Locale locale = new Locale("ru");
        formatOfDay = new SimpleDateFormat("E d.MMM", locale);
        formatOfWeek = new SimpleDateFormat("E", locale);
    }

    @Override
    public void execute(Update update) {
        dayOfWeek = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        timeOfWorkout = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        String workoutTableName = dayOfWeek + timeOfWorkout;
        workout = workoutService.findWorkoutByTime(dayOfWeek, timeOfWorkout);
        maxSize = workout.getMaxCountUser();
        userService.findUsersByRole("COACH").stream()
                .findFirst().ifPresent(coach -> coachChatId = coach.getId());
        String command = Objects.requireNonNull(getCallbackQuery(update)).split("/")[3];

        if (!TelegramBot.getInstance().notifyMessageId.isEmpty()) {
            Message msg = Objects.requireNonNull(TelegramBot.getInstance().notifyMessageId.poll());
            boolean complete = sendMessageService.deleteMessage(getChatId(update), msg.getMessageId());
            if (!complete) {
                sendMessageService.deleteMessage(msg.getChatId().toString(), msg.getMessageId());
            }
        }
        if (!validWorkout(workoutTableName, getMessageId(update))) {
            return;
        }
        List<NewWorkout> joinedUsers = workoutService.findAllJoinedUsers(workout.getId());
        long countNotReserve = joinedUsers.stream()
                .filter(f -> (!f.isReserve())).count();

        freePlaces = maxSize - (int) countNotReserve;
        workoutService.findJoinedUser(getChatId(update), workout.getId()).ifPresentOrElse(
                people -> {
                    if (command.equals("cancel")) {
                        workoutService.delete(people);
                        addStatistic(update, REMOVE.getUserAction());
                        if (!people.isReserve() && freePlaces >= 0) {
                            freePlaces++;
                            workoutService.findAllJoinedUsers(workout.getId()).stream()
                                    .filter(NewWorkout::isReserve).findFirst()
                                    .ifPresent(user -> {
                                        user.setReserve(false);
                                        workoutService.updateNewWorkout(user);
                                        freePlaces--;
                                        String notification = "\uD83E\uDD73  Место освободилось! Ждем на тренировке в " + dayOfWeek + " в " + timeOfWorkout;
                                        sendMessageService.sendMessage(user.getChatId(), notification, null);
                                    });
                        }
                        callback = ENJOY.getCommandName() + "/" + dayOfWeek + "/" + timeOfWorkout + "/join";
                        String text = freePlaces <= 0 ? "Записаться в резерв" : "Записаться";
                        board = builder().add(text, callback).create();

                    }
                },
                () -> {
                    boolean isReserve;
                    if (command.equals("join")) {
                        isReserve = joinedUsers.stream()
                                .filter(f -> (!f.isReserve())).count() >= maxSize;
                        userService.findSentMessage(getChatId(update), workout.getId())
                                .ifPresent(sentMessages -> workoutService.saveNewWorkout(createEntity(update, isReserve, sentMessages, workout)));
                        if (!isReserve) {
                            addStatistic(update, ADD.getUserAction());
                            freePlaces--;
                        } else {
                            addStatistic(update, ADD_TO_RESERVE.getUserAction());
                        }
                        callback = ENJOY.getCommandName() + "/" + dayOfWeek + "/" + timeOfWorkout + "/cancel";
                        board = builder().add("Отмена", callback).create();
                    }
                });
        workoutList = workoutService.findAllJoinedUsers(workout.getId());

        String message = createListUsers(workoutList, freePlaces, getChatId(update));
        sendMessageService.editMessage(getChatId(update), getMessageId(update), message, board);

        editMessageForAllUsers(getChatId(update), workout.getId());
    }

    private String createListUsers(List<NewWorkout> users, int places, String chatId) {
        AtomicInteger number = new AtomicInteger(0);
        String date;
        if (dayOfWeek.equals(formatOfWeek.format(System.currentTimeMillis()))) {
            date = formatOfDay.format(System.currentTimeMillis());
        } else {
            date = formatOfDay.format(System.currentTimeMillis() + ONE_DAY);
        }
        StringBuilder message = new StringBuilder("Запись на тренировку в " + date + " в <strong>" + timeOfWorkout + "</strong> открыта!\n "
                + "Количество свободных мест:   %s \nСписок записавшихся: \n");
        users.stream().filter(f -> (!f.isReserve()))
                .forEach(u -> {
                    if (u.getChatId().equals(chatId)) {
                        message.append(number.incrementAndGet()).append(". ").append("<i>Я</i>\n");
                    } else
                        message.append(number.incrementAndGet()).append(". ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
                });
        number.set(0);
        List<NewWorkout> reserve = users.stream().filter(NewWorkout::isReserve).toList();
        if (!reserve.isEmpty()) {
            message.append("<strong>Резерв:</strong>\n");
            reserve.forEach(u -> {
                if (u.getChatId().equals(chatId)) {
                    message.append(number.incrementAndGet()).append(". ").append("<i>Я</i>\n");
                } else
                    message.append(number.incrementAndGet()).append(". ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
            });
        }

        return String.format(message.toString(), Symbols.getSymbol(Math.max(places, 0)));
    }

    private void editMessageForAllUsers(String chatId, Long workoutId) {
        AtomicReference<InlineKeyboardMarkup> board = new AtomicReference<>();
        userService.findSentMessages(workoutId)
                .forEach(sentMessages -> {
                    if (sentMessages.getUser().getId().equals(coachChatId)) {
                        board.set(builder().add("Отменить тренировку", "cancel_workout/" +
                                sentMessages.getWorkout().getDayOfWeek() + "/"
                                + sentMessages.getWorkout().getTime())
                                .create());
                        sendMessageService.editMessage(sentMessages.getUser().getId(),
                                sentMessages.getMessageId(),
                                createListUsers(workoutList, freePlaces, sentMessages.getUser().getId()),
                                board.get());
                    } else {
                        if (sentMessages.getUser().isEqualsRole("USER") && !sentMessages.getUser().getId().equals(chatId)) {
                            workoutService.findJoinedUser(sentMessages.getUser().getId(), workoutId).ifPresentOrElse(user -> {
                                board.set(builder().add("Отмена", ENJOY.getCommandName() + "/" +
                                        sentMessages.getWorkout().getDayOfWeek() + "/" +
                                        sentMessages.getWorkout().getTime() +
                                        "/cancel")
                                        .create());
                                sendMessageService.editMessage(sentMessages.getUser().getId(),
                                        sentMessages.getMessageId(),
                                        createListUsers(workoutList, freePlaces, sentMessages.getUser().getId()),
                                        board.get());
                            }, () -> {
                                String text = freePlaces <= 0 ? "Записаться в резерв" : "Записаться";
                                board.set(builder().add(text, ENJOY.getCommandName() + "/"
                                        + sentMessages.getWorkout().getDayOfWeek() + "/" +
                                        sentMessages.getWorkout().getTime() +
                                        "/join")
                                        .create());
                                sendMessageService.editMessage(sentMessages.getUser().getId(),
                                        sentMessages.getMessageId(),
                                        createListUsers(workoutList, freePlaces, sentMessages.getUser().getId()),
                                        board.get());
                            });
                        }
                    }
                });
    }

    private NewWorkout createEntity(Update update, boolean reserve, SentMessages sentMessages, Workout workout) {
        NewWorkout newWorkout = new NewWorkout();
        newWorkout.setChatId(getChatId(update));
        newWorkout.setFirstName(getFirstName(update));
        if (getLastName(update) != null) {
            newWorkout.setLastName(getLastName(update));
        } else {
            newWorkout.setLastName("");
        }
        newWorkout.setReserve(reserve);
        newWorkout.setSentMessages(sentMessages);
        newWorkout.setWorkout(workout);
        return newWorkout;
    }

    private void addStatistic(Update update, String action) {
        stat.setChatId(getChatId(update));
        String lastName = getLastName(update) == null ? "" : getLastName(update);
        stat.setUserName(getFirstName(update) + " " + lastName);
        stat.setWorkout(dayOfWeek + " " + timeOfWorkout);
        stat.setAction(action);
        stat.setDate(Date.format(System.currentTimeMillis()));
        userService.saveStatistic(stat);
    }

    private boolean validWorkout(String nameOfWorkout, Integer messageId) {
        AtomicBoolean isValid = new AtomicBoolean(false);
        workoutService.findAllWorkout()
                .forEach(w -> {
                    if ((w.getDayOfWeek() + w.getTime()).equals(nameOfWorkout) && w.isActive()) {
                        if (userService.findByMessageId(messageId).isPresent())
                            isValid.set(true);
                    }
                });
        return isValid.get();
    }
}
