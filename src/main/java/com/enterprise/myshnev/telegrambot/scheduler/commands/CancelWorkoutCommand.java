package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.NewWorkoutTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.Data;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Objects;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

public class CancelWorkoutCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    public final WorkoutService workoutService;
    private InlineKeyboardMarkup board;
    private NewWorkoutTable newWorkoutTable;

    public CancelWorkoutCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        newWorkoutTable = new NewWorkoutTable();
    }

    @Override
    public void execute(Update update) {
        String dayOfWeek = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        String time = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        String message = "❗❗❗Вы действительно хотите отменить тренировку?";
        board = builder().add("Да", "confirm/yes/" + dayOfWeek + "/" + time)
                .add("Нет", "confirm/no/" + dayOfWeek + "/" + time).create();
        Long countUser = workoutService.findAll(dayOfWeek + time, newWorkoutTable).stream()
                .map(m -> (NewWorkout) m).filter(f -> (!f.isReserve())).count();
        sendMessageService.getData(getChatId(update)).stream()
                .filter(f -> (f.getDayOfWeek().equals(dayOfWeek) && f.getTimeWorkout().equals(time)))
                .findFirst().ifPresent(p -> {
            sendMessageService.editMessage(getChatId(update),getMessageId(update), String.format(p.getMessage(), Symbols.getSymbol((p.getMaxUser() - countUser.intValue()))),null);
            sendMessageService.sendMessage(new Data(getChatId(update), message, board, time, dayOfWeek, p.getMaxUser(), true));
        });

    }
}
