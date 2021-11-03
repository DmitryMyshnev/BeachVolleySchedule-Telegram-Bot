package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;
import com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.getCallbackQuery;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.getMessageId;

import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class EditWorkoutCommand implements Command {
    private final SendMessageService sendMessageService;
    private final WorkoutService workoutService;
    private String message;
    private InlineKeyboardMarkup board;
    private WorkoutsTable workoutsTable;

    public EditWorkoutCommand(SendMessageService sendMessageService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.workoutService = workoutService;
        workoutsTable = new WorkoutsTable();
    }

    @Override
    public void execute(Update update) {
        String command = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        String weekOfDay = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        String time = Objects.requireNonNull(getCallbackQuery(update)).split("/")[3];
        String chatId = getChatId(update);
        Integer messageId = getMessageId(update);
        if (command.equals("back")) {
            message = createMessage();
            getWorkouts();
            sendMessageService.editMessage(chatId, messageId, message, board);
            return;
        }
        if (command.equals("delete")) {
            workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream()
                    .map(w -> (Workouts) w)
                    .filter(w -> (w.getDayOfWeek().equals(weekOfDay) && w.getTime().equals(time)))
                    .findFirst()
                    .ifPresent(p -> {
                        if (!p.isActive()){
                            workoutService.delete(WORKOUT.getTableName(), p.getId().toString(), workoutsTable);
                            message = createMessage();
                            getWorkouts();
                            sendMessageService.editMessage(chatId, messageId, message, board);
                        }else {
                            sendMessageService.sendMessage(getChatId(update),"❗Тренировка в активном состоянии, ее нельзя удалить сейчас!",null);
                        }
                    });
        }
    }

    private String createMessage() {
        List<Workouts> workout = workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream()
                .map(w -> (Workouts) w)
                .collect(Collectors.toList());
        AtomicInteger size = new AtomicInteger(0);
        StringBuilder mess = new StringBuilder();
        mess.append("<strong>Рассписание тренировок:</strong>\n");
        WEEK.forEach(week -> {
            workout.stream().filter(sh -> (sh.getDayOfWeek().equals(week))).forEach(e -> {
                if (size.get() == 0) {
                    mess.append("<i>").append(WEEK_FULL_NAME.get(week)).append("</i>").append(" - ").append(e.getTime());
                    size.incrementAndGet();
                } else {
                    mess.append(", ").append(e.getTime());
                }
            });
            if (size.get() > 0)
                mess.append("\n");
            size.set(0);
        });
        mess.append("\n Выберете для редкатирования: \n");
        return mess.toString();
    }

    private void getWorkouts() {
        InlineKeyBoard button = new InlineKeyBoard();
        List<Workouts> workout = workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream()
                .map(w -> (Workouts) w)
                .collect(Collectors.toList());
        WEEK.forEach(week -> workout.stream().filter(f->(f.getDayOfWeek().equals(week))).forEach(w->{
        String text = WEEK_FULL_NAME.get(w.getDayOfWeek()) + " " + w.getTime();
        board = button.addButton(text, "editWorkout/" + w.getDayOfWeek() + "/" + w.getTime());
        }));
        board = button.addButton("Добавить тренировку", "add_workout");
    }
}
