package com.enterprise.myshnev.telegrambot.scheduler.timer;

import com.enterprise.myshnev.telegrambot.scheduler.commands.SuperAdminUtils;
import com.enterprise.myshnev.telegrambot.scheduler.commands.Symbols;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.ENJOY;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

public class Task extends TimerTask {

    private final static Map<String, Integer> WEEK = ImmutableMap.<String, Integer>builder()
            .put("пн", 1)
            .put("вт", 2)
            .put("ср", 3)
            .put("чт", 4)
            .put("пт", 5)
            .put("сб", 6)
            .put("вс", 7)
            .put("mo", 1)
            .put("tu", 2)
            .put("wed", 3)
            .put("thu", 4)
            .put("fri", 5)
            .put("sat", 6)
            .put("sun", 7).build();
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private String HOUR_OF_NOTIFICATION;
    private InlineKeyboardMarkup board;
    private final SimpleDateFormat formatOfWeek;
    private final StopTimerTack stopTimerTack;
    private final SimpleDateFormat formatOfHour;

    public static Logger LOGGER = LogManager.getLogger(Task.class);

    public Task(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;
        this.stopTimerTack = new StopTimerTack(sendMessageService, userService, workoutService);
        formatOfWeek = new SimpleDateFormat("u");
        formatOfHour = new SimpleDateFormat("H:mm");

        // HOUR_OF_NOTIFICATION = SuperAdminUtils.getTimeNotificationFromFileConfig();
    }

    @Override
    public void run() {
        HOUR_OF_NOTIFICATION = SuperAdminUtils.TIME_OF_NOTIFICATION;
        List<Workout> workouts = workoutService.findAllWorkout();
        String currentDayOfWeek = formatOfWeek.format(System.currentTimeMillis());
        if (formatOfHour.format(System.currentTimeMillis()).equals(HOUR_OF_NOTIFICATION)) {
            workouts.forEach(workout -> {
                //int dayOfNotification = (WEEK.get(workout.getDayOfWeek()) - 1) == 0 ? 7 : WEEK.get(workout.getDayOfWeek()) - 1;
                 int dayOfNotification = WEEK.get(workout.getDayOfWeek());
                if (Integer.parseInt(currentDayOfWeek) == dayOfNotification) {
                    workout.setActive(true);
                    workoutService.updateWorkout(workout);
                    createNotification(workout);
                }
            });
        }
        workouts.forEach(w -> {
            int dayOfWorkout = WEEK.get(w.getDayOfWeek());
            if (w.isActive()) {
                if (Integer.parseInt(currentDayOfWeek) == dayOfWorkout && formatOfHour.format(System.currentTimeMillis() + HOURS.toMillis(1)).equals(w.getTime())) {
                    stopTimerTack.breakWorkout(w);
                }
            }
        });
    }

    private void createNotification( Workout workout) {
        Locale locale = new Locale("ru");
        String date = new SimpleDateFormat("E d MMM", locale).format(System.currentTimeMillis() + DAYS.toMillis(1));
        String callback = ENJOY.getCommandName() + "/" + workout.getDayOfWeek() + "/" + workout.getTime() + "/join";
        String message = "Запись на тренировку в " + date + " в " + workout.getTime() + " открыта!\n " +
                "Количество свободных мест: " + Symbols.getSymbol(workout.getMaxCountUser());
        // workoutService.addTable(dayOfWeek + time, newWorkoutsTable);
        userService.findAllByActive(true).forEach(user -> {
            if (user.isEqualsRole("COACH")) {
                board = builder().add("Отменить тренировку", "cancel_workout/" + workout.getDayOfWeek() + "/" + workout.getTime()).create();
                sendMessageService.sendMessage(user, message, workout, board);
            } else {
                if (!user.isEqualsRole("ADMIN")) {
                    board = builder().add("Записаться", callback).create();
                    sendMessageService.sendMessage(user, message, workout,board);
                }
            }
        });
    }
}
