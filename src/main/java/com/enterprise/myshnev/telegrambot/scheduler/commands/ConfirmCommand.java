package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.model.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
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

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

public class ConfirmCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private String message;
    private Integer messageId;
    private String timeOfWorkout;
    private String dayOfWeek;
    private final SimpleDateFormat formatOfDay;
    private final SimpleDateFormat formatOfWeek;
    private static final Long ONE_DAY = 86400000L;

    public ConfirmCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        formatOfDay = new SimpleDateFormat("E d MMM", new Locale("ru"));
        formatOfWeek = new SimpleDateFormat("E", new Locale("ru"));
    }

    @Override
    public void execute(Update update) {
        String answer = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        dayOfWeek = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        timeOfWorkout = Objects.requireNonNull(getCallbackQuery(update)).split("/")[3];
        messageId = getMessageId(update);
        Workout workout = workoutService.findWorkoutByTime(dayOfWeek, timeOfWorkout);
        Long workoutId = workout.getId();
        if (answer.equals("yes")) {
            workout.setActive(false);
            workoutService.updateWorkout(workout);
            sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
            workoutService.findAllJoinedUsers(workoutId).forEach(workoutService::deleteNewWorkout);
            sendMessageForAllUsers(workoutId);
        } else {
            long maxSize = workout.getMaxCountUser();
            List<NewWorkout> listUsers = workoutService.findAllJoinedUsers(workoutId);
            long count = listUsers.stream().filter(f -> (!f.isReserve())).count();
            long freePlaces = maxSize - count;

            String message = createListUsers(listUsers, (int) freePlaces);
            AtomicReference<Integer> messageId = new AtomicReference<>();
            userService.findByUserIdAndWorkoutId(getChatId(update), workoutId).ifPresent(p -> messageId.set(p.getMessageId()));

            InlineKeyboardMarkup board = builder().add("Отменить тренировку", "cancel_workout/" + dayOfWeek + "/" + timeOfWorkout).create();
            sendMessageService.editMessage(getChatId(update), messageId.get(), message, board);
            sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
        }

        if (!TelegramBot.getInstance().notifyMessageId.isEmpty()) {
            sendMessageService.deleteMessage(getChatId(update), Objects.requireNonNull(TelegramBot.getInstance().notifyMessageId.poll()).getMessageId());
        }
    }

    private void sendMessageForAllUsers(Long workoutId) {
        String date;
        if (dayOfWeek.equals(formatOfWeek.format(System.currentTimeMillis()))) {
            date = formatOfDay.format(System.currentTimeMillis());
        } else {
            date = formatOfDay.format(System.currentTimeMillis() + ONE_DAY);
        }
        message = "❌ Тренировка  " + date + " в " + timeOfWorkout + " отменена!";
        userService.findByWorkoutId(workoutId)
                .forEach(sentMessages -> {
                    String chatId = sentMessages.getUser().getId();
                    sendMessageService.deleteWorkoutMessage(chatId, sentMessages);
                    sendMessageService.sendMessage(chatId, message, null);
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
