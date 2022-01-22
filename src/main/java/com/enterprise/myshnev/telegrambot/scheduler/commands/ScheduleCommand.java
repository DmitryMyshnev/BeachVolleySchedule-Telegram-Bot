package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class ScheduleCommand implements Command {
    private final SendMessageService sendMessageService;
    private final WorkoutService workoutService;
    private final UserService userService;
    private final String SUPER_ADMIN;

    private String message = "<strong>Рассписание тренировок:</strong>\n";
    private InlineKeyboardMarkup board;

    public ScheduleCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        SUPER_ADMIN = SuperAdminUtils.getIdSuperAdminFromFileConfig();
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(getChatId(update))
                .ifPresent(user -> {
                    if (user.isEqualsRole("COACH")) {
                        getWorkoutsForCoach(update);
                    } else {
                        getWorkoutsForUser(update);
                    }
                });
    }

    private void getWorkoutsForUser(Update update) {
        List<Workout> workout = workoutService.findAllWorkout();
        message = createMessage(workout);
        sendMessageService.sendMessage(getChatId(update), message, null);
    }

    private void getWorkoutsForCoach(Update update) {
        InlineKeyBoard button = new InlineKeyBoard();
        List<Workout> workout = workoutService.findAllWorkout();
        WEEK.forEach(week -> workout.stream().filter(f -> (f.getDayOfWeek().equals(week))).forEach(w -> {
            String text = WEEK_FULL_NAME.get(w.getDayOfWeek()) + " " + w.getTime();
            board = button.addButton(text, "editWorkout/" + w.getDayOfWeek() + "/" + w.getTime());
        }));
        board = button.addButton("Добавить тренировку", "add_workout/");
        message = createMessage(workout);
        if (!workout.isEmpty()) {
            message += "\n Выберете для редактирования:\n";
        }
        sendMessageService.sendMessage(getChatId(update), message, board);
    }

    private String createMessage(List<Workout> workout) {
        AtomicInteger size = new AtomicInteger(0);
        StringBuilder mess = new StringBuilder();

        if(workout.isEmpty()){
            mess.append("Тренировок не найдно \uD83E\uDD37\u200D♂️");
        }else {
            mess.append("<strong>Рассписание тренировок:</strong>\n");
            WEEK.forEach(week -> {
                workout.stream().filter(sh -> (sh.getDayOfWeek().equals(week))).forEach(e -> {
                    if (size.get() == 0) {
                        mess.append("<i>").append(WEEK_FULL_NAME.get(week)).append(" - ").append(e.getTime()).append("</i>");
                        size.incrementAndGet();
                    } else {
                        mess.append(", ").append(e.getTime());
                    }
                });
                if (size.get() > 0)
                    mess.append("\n");
                size.set(0);
            });
        }
        return mess.toString();
    }
}
