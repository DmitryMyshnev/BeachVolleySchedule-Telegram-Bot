package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.Data;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Objects;
import java.util.Queue;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.WORKOUTS;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.WORKOUT;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

public class CancelWorkoutCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    public final WorkoutService workoutService;
    private InlineKeyboardMarkup board;
    private final NewWorkoutTable newWorkoutTable;
    private final WorkoutsTable workoutTable;

    public CancelWorkoutCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        newWorkoutTable = new NewWorkoutTable();
        this.workoutTable = new WorkoutsTable();
    }

    @Override
    public void execute(Update update) {
        String dayOfWeek = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        String time = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        String message = "❗Вы действительно хотите отменить тренировку в " + dayOfWeek + " в " + time + "?";
        board = builder().add("Да", "confirm/yes/" + dayOfWeek + "/" + time)
                .add("Нет", "confirm/no/" + dayOfWeek + "/" + time).create();
        Long countUser = workoutService.findAll(dayOfWeek + time, newWorkoutTable).stream()
                .map(m -> (NewWorkout) m).filter(f -> (!f.isReserve())).count();
        workoutService.findAll(WORKOUT.getTableName(),workoutTable).stream().map(Workouts.class::cast)
                .filter(f -> (f.getDayOfWeek().equals(dayOfWeek) && f.getTime().equals(time)))
                .findFirst().ifPresent(p -> {
            sendMessageService.editMessage(getChatId(update),getMessageId(update), String.format(getText(update), Symbols.getSymbol((p.getMaxCountUser() - countUser.intValue()))),null);
            sendMessageService.sendMessage(getChatId(update), message, board);
        });

        if (!TelegramBot.getInstance().notifyMessageId.isEmpty()) {
            sendMessageService.deleteMessage(getChatId(update), Objects.requireNonNull(TelegramBot.getInstance().notifyMessageId.poll()).getMessageId());
        }
    }
}
