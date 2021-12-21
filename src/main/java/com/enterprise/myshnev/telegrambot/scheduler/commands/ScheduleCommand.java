package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.AdminTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.WorkoutsTable;
import com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ScheduleCommand implements Command {
    private final SendMessageService sendMessageService;
    private final WorkoutService workoutService;
    private final UserService userService;
    private final UserTable userTable;
    private final WorkoutsTable  workoutsTable;
    private final String SUPER_ADMIN;

    private String message = "<strong>Рассписание тренировок:</strong>\n";
    private InlineKeyboardMarkup board;

    public ScheduleCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        userTable = new UserTable();
        workoutsTable = new WorkoutsTable();
        SUPER_ADMIN = SuperAdminUtils.getIdSuperAdminFromFileConfig();
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(USERS.getTableName(), getChatId(update), userTable).map(m -> (TelegramUser) m)
                .ifPresentOrElse(user -> {
                    if (user.isCoach()) {
                        getWorkoutsForCoach(update);
                    } else {
                        getWorkoutsForUser(update);
                    }
                },()-> userService.findByChatId(ADMIN.getTableName(), getChatId(update),new AdminTable()).map(TelegramUser.class::cast)
                        .ifPresent(admin-> getWorkoutsForCoach(update)));
      /*  if(getChatId(update).equals(SUPER_ADMIN)){
            getWorkoutsForCoach(update);
        }*/
    }

    private void getWorkoutsForUser(Update update) {
        List<Workouts> workout = workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream().
                map(w -> (Workouts) w)
                .collect(Collectors.toList());
        message = createMessage(workout);
        sendMessageService.sendMessage(getChatId(update), message, null);
    }

    private void getWorkoutsForCoach(Update update) {
        InlineKeyBoard button = new InlineKeyBoard();
        List<Workouts> workout = workoutService.findAll(WORKOUT.getTableName(), workoutsTable).stream()
                .map(w -> (Workouts) w)
                .collect(Collectors.toList());
        WEEK.forEach(week -> workout.stream().filter(f->(f.getDayOfWeek().equals(week))).forEach(w->{
            String text = WEEK_FULL_NAME.get(w.getDayOfWeek()) + " " + w.getTime();
            board = button.addButton(text, "editWorkout/" + w.getDayOfWeek() + "/" + w.getTime());
        }));
        board = button.addButton("Добавить тренировку", "add_workout/");
        message = createMessage(workout);
        if(!workout.isEmpty()) {
            message += "\n Выберете для редактирования:\n";
        }
        sendMessageService.sendMessage(getChatId(update), message, board);
    }

    private String createMessage(List<Workouts> workout) {
        AtomicInteger size = new AtomicInteger(0);
        StringBuilder mess = new StringBuilder();
        mess.append( "<strong>Рассписание тренировок:</strong>\n");
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
        return mess.toString();
    }
}
