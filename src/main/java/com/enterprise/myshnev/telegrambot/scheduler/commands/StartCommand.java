package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.model.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.model.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.ENJOY;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

public class StartCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final WorkoutService workoutService;
    private String message;
    private InlineKeyboardMarkup board;
    private String callback;
    private String timeOfWorkout;
    private String dayOfWeek;
    private static final Long ONE_DAY = 86400000L;
    private final SimpleDateFormat formatOfDay;
    private final SimpleDateFormat formatOfWeek;

    public StartCommand(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        this.workoutService = workoutService;

        formatOfDay = new SimpleDateFormat("E d.MMM", new Locale("ru"));
        formatOfWeek = new SimpleDateFormat("E", new Locale("ru"));
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(getChatId(update)).ifPresentOrElse(user -> {
            switch (user.getRole().getName()) {
                case "COACH" -> {
                    message = "????????????, " + user.getFirstName() + "! ";
                    sendMessageService.sendMessage(user.getId(), message, null);
                }
                case "ADMIN" -> sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
                default -> {
                    if (user.isActive()) {
                        message = "???? ?????? ????????????????????????????????";
                        sendMessageService.sendMessage(user.getId(), message, null);
                    } else {
                        user.setActive(true);
                        userService.updateUser(user);
                        message = "?????????????????????? ????????????????.";
                        sendMessageService.sendMessage(user.getId(), message, null);
                        findActiveWorkout(user);
                    }
                }
            }
        }, () -> {
            TelegramUser newUser = new TelegramUser(getChatId(update), getFirstName(update), getLastName(update), userService.findRoleByName("USER"));

            userService.saveUser(newUser);
            List<TelegramUser> coach = userService.findUsersByRole("COACH");
            StringBuilder nameCoach = new StringBuilder();
            if (coach.size() > 0) {
                nameCoach.append("?? ??????????????:\n ")
                        .append(coach.get(0).getFirstName()).append(" ").append(coach.get(0).getLastName() == null ? "" : coach.get(0).getLastName());
            }
            message = "????????????, " + getFirstName(update) + "! ???????? ?????? ?????????????? ???????? ???????????????????????? ???? ????????????????????   " + nameCoach +
                    "\n /help - ???????????????????? ?????????????????? ?? ????????\n" +
                    "/workouts - ???????????????????? ?????????????????????? ????????????????????\n" +
                    "/stop - ?????????????????? ??????????????????????\n" +
                    "/start - ???????????????? ??????????????????????\n";
            sendMessageService.sendMessage(getChatId(update), message, null);
            findActiveWorkout(newUser);
        });
        message = "";
    }

    private void findActiveWorkout(TelegramUser user) {
        workoutService.findAllWorkout().stream()
                .filter(Workout::isActive)
                .forEach(workouts -> {
                    if (userService.findByUserIdAndWorkoutId(user.getId(), workouts.getId()).isEmpty()) {
                        timeOfWorkout = workouts.getTime();
                        dayOfWeek = workouts.getDayOfWeek();
                        callback = ENJOY.getCommandName() + "/" + workouts.getDayOfWeek() + "/" + timeOfWorkout + "/join";
                        List<NewWorkout> signedUpUsers = workoutService.findAllJoinedUsers(workouts.getId());
                        long freePlaces = workouts.getMaxCountUser() - signedUpUsers.stream().filter(r -> !r.isReserve()).count();
                        String mess = createListUsers(signedUpUsers, (int) freePlaces, user.getId());
                        String buttonText = freePlaces <= 0 ? "???????????????????? ?? ????????????" : "????????????????????";
                        board = builder().add(buttonText, callback).create();
                        Integer id = sendMessageService.sendMessage(user, mess, workouts, board);
                        if (id == 0) {
                            sendMessageService.sendMessage(user, mess, workouts, board);
                        }
                    }
                });
    }

    private String createListUsers(List<NewWorkout> users, int places, String chatId) {
        AtomicInteger number = new AtomicInteger(0);
        String date;
        if (dayOfWeek.equals(formatOfWeek.format(System.currentTimeMillis()))) {
            date = formatOfDay.format(System.currentTimeMillis());
        } else {
            date = formatOfDay.format(System.currentTimeMillis() + ONE_DAY);
        }
        StringBuilder message = new StringBuilder("???????????? ???? ???????????????????? ?? " + date + " ?? " + timeOfWorkout + " ??????????????!\n "
                + "???????????????????? ?????????????????? ????????:   %s \n???????????? ????????????????????????: \n");

        users.stream().filter(f -> (!f.isReserve()))
                .forEach(u -> {
                    if (u.getChatId().equals(chatId)) {
                        message.append(number.incrementAndGet()).append(". ").append("<i>??</i>\n");
                    } else
                        message.append(number.incrementAndGet()).append(". ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
                });
        number.set(0);
        List<NewWorkout> reserve = users.stream().filter(NewWorkout::isReserve).toList();
        if (!reserve.isEmpty()) {
            message.append("<strong>????????????:</strong>\n");
            reserve.forEach(u -> {
                if (u.getChatId().equals(chatId)) {
                    message.append(number.incrementAndGet()).append(". ").append("<i>??</i>\n");
                } else
                    message.append(number.incrementAndGet()).append(". ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
            });
        }
        return String.format(message.toString(), Symbols.getSymbol(Math.max(places, 0)));
    }
}
