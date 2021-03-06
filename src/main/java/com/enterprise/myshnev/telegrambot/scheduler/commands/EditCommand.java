package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Objects;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

public class EditCommand implements Command{
    private final SendMessageService sendMessageService;
    private final WorkoutService workoutService;

    public EditCommand(SendMessageService sendMessageService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.workoutService = workoutService;
    }

    @Override
    public void execute(Update update) {
        String weekOfDay = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
        String time = Objects.requireNonNull(getCallbackQuery(update)).split("/")[2];
        Integer messageId = getMessageId(update);
        String message;
      //  List<Workouts> workout = workoutService.findAllWorkout();
        message = "\n Выберете действие для тренировки: \n" +"<i>"+ WEEK_FULL_NAME.get(weekOfDay) + " " + time + "</i>";
        InlineKeyboardMarkup board = builder()
                .add("Удалить","changeWorkout/delete/"  + weekOfDay + "/" + time,true )
                .add("⬅  Назад к расписанию","changeWorkout/back/" + weekOfDay + "/" + time ).create();
        sendMessageService.editMessage(getChatId(update),messageId ,message,board);

        if (!TelegramBot.getInstance().notifyMessageId.isEmpty()) {
            sendMessageService.deleteMessage(getChatId(update), Objects.requireNonNull(TelegramBot.getInstance().notifyMessageId.poll()).getMessageId());
        }
    }

}
