package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class EditWorkoutCommand implements Command {
    private final SendMessageService sendMessageService;
    private final WorkoutService workoutService;
    private String message;
    private InlineKeyboardMarkup board;

    public EditWorkoutCommand(SendMessageService sendMessageService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.workoutService = workoutService;
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

        }
        if (command.equals("delete")) {
            workoutService.findAllWorkout().stream()
                    .filter(w -> (w.getDayOfWeek().equals(weekOfDay) && w.getTime().equals(time)))
                    .findFirst()
                    .ifPresent(p -> {
                        if (!p.isActive()){
                            workoutService.deleteWorkout(p);
                            message = createMessage();
                            getWorkouts();
                            sendMessageService.editMessage(chatId, messageId, message, board);
                        }else {
                            sendMessageService.sendMessage(getChatId(update),"❗Тренировка в активном состоянии, ее нельзя удалить сейчас!",null);
                        }
                    });
        }

        if (!TelegramBot.getInstance().notifyMessageId.isEmpty()) {
            sendMessageService.deleteMessage(getChatId(update), Objects.requireNonNull(TelegramBot.getInstance().notifyMessageId.poll()).getMessageId());
        }
    }

    private String createMessage() {
        List<Workout> workout = workoutService.findAllWorkout();
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
        if(!workout.isEmpty()) {
            mess.append("\n Выберете для редактирования: \n");
        }
        return mess.toString();
    }

    private void getWorkouts() {
        InlineKeyBoard button = new InlineKeyBoard();
        List<Workout> workout = workoutService.findAllWorkout();
        WEEK.forEach(week -> workout.stream().filter(f->(f.getDayOfWeek().equals(week))).forEach(w->{
        String text = WEEK_FULL_NAME.get(w.getDayOfWeek()) + " " + w.getTime();
        board = button.addButton(text, "editWorkout/" + w.getDayOfWeek() + "/" + w.getTime());
        }));
        board = button.addButton("Добавить тренировку", "add_workout");
    }
}
