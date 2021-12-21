package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.MessageIdTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.MessageId;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.SimpleDateFormat;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

public class ConfirmCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private String message;
    private Integer messageId;
    private String timeOfWorkout;
    private String dayOfWeek;
    private final NewWorkoutTable newWorkoutTable;
    private final MessageIdTable messageIdTable;
    private final SimpleDateFormat formatOfDay;
    private final UserTable userTable;
    private final WorkoutsTable workoutsTable;
    private final SimpleDateFormat formatOfWeek;
    private static final Long ONE_DAY = 86400000L;

    public ConfirmCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        newWorkoutTable = new NewWorkoutTable();
        formatOfDay = new SimpleDateFormat("E d MMM", new Locale("ru"));
        formatOfWeek = new SimpleDateFormat("E", new Locale("ru"));
        userTable = new UserTable();
        workoutsTable = new WorkoutsTable();
        messageIdTable = new MessageIdTable();
    }

    @Override
    public void execute(Update update) {
        String answer = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        dayOfWeek = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        timeOfWorkout = Objects.requireNonNull(getCallbackQuery(update)).split("/")[3];
        messageId = getMessageId(update);
        String tableName = dayOfWeek + timeOfWorkout;
        if (answer.equals("yes")) {
            workoutService.dropTable(tableName, newWorkoutTable);
            workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream()
                    .map(m -> (Workouts) m)
                    .filter(f -> (f.getDayOfWeek().equals(dayOfWeek) && f.getTime().equals(timeOfWorkout))).findFirst().ifPresent(w -> {
                workoutService.update(workoutsTable, WORKOUT.getTableName(), w.getId().toString(), "active", "0");
            });
            sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
            sendMessageForAllUsers();
        } else {
            AtomicInteger maxSize = new AtomicInteger(0);
            List<NewWorkout> listUsers = workoutService.findAll(dayOfWeek + timeOfWorkout, newWorkoutTable).stream()
                    .map(NewWorkout.class::cast).collect(Collectors.toList());

            Long count = workoutService.findAll(dayOfWeek + timeOfWorkout, newWorkoutTable).stream().map(m -> (NewWorkout) m)
                    .filter(f -> (!f.isReserve())).count();
            workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream().map(m -> (Workouts) m)
                    .filter(wts -> (wts.getDayOfWeek().equals(dayOfWeek) && wts.getTime().equals(timeOfWorkout))).findFirst()
                    .ifPresent(ps -> maxSize.set(ps.getMaxCountUser()));
            Long freePlaces = maxSize.get() - count;

            String message = createListUsers(listUsers, freePlaces.intValue());
            AtomicReference<Integer> messageId = new AtomicReference<>();
            workoutService.findBy(MESSAGE_ID.getTableName(), "chat_id", getChatId(update), messageIdTable).stream()
                    .map(MessageId.class::cast).filter(f -> (f.getDayOfWeek().equals(dayOfWeek) && f.getTime().equals(timeOfWorkout)))
                    .findFirst().ifPresent(id -> messageId.set(id.getMessageId()));

            InlineKeyboardMarkup board = builder().add("Отменить тренировку", "cancel_workout/" + dayOfWeek + "/" + timeOfWorkout).create();
            sendMessageService.editMessage(getChatId(update), messageId.get(), message, board);
            sendMessageService.deleteWorkoutMessage(getChatId(update), getMessageId(update));
        }

        if (!TelegramBot.getInstance().notifyMessageId.isEmpty()) {
            sendMessageService.deleteMessage(getChatId(update), Objects.requireNonNull(TelegramBot.getInstance().notifyMessageId.poll()).getMessageId());
        }
    }

    private void sendMessageForAllUsers() {
        String date;
        if (dayOfWeek.equals(formatOfWeek.format(System.currentTimeMillis()))) {
            date = formatOfDay.format(System.currentTimeMillis());
        } else {
            date = formatOfDay.format(System.currentTimeMillis() + ONE_DAY);
        }
        message = "❌ Тренировка  " + date + " в " + timeOfWorkout + " отменена!";
        userService.findAll(MESSAGE_ID.getTableName(), messageIdTable).stream().map(MessageId.class::cast).forEach(user -> {
            sendMessageService.deleteWorkoutMessage(user.getChatId(), user.getMessageId());
            sendMessageService.sendMessage(user.getChatId(), message, null);
        });
    }

    private String createListUsers(List<NewWorkout> users, int places) {
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
                    message.append(number.incrementAndGet()).append(". ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
                });
        number.set(0);
        List<NewWorkout> reserve = users.stream().filter(NewWorkout::isReserve).toList();
        if (!reserve.isEmpty()) {
            message.append("<strong>Резерв:</strong>\n");
            reserve.forEach(u -> {
                message.append(number.incrementAndGet()).append(". ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
            });
        }

        return String.format(message.toString(), Symbols.getSymbol(Math.max(places, 0)));
    }
}
