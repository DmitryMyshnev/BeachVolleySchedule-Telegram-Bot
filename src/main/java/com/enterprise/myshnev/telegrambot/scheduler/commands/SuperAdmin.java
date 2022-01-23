package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.model.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.ADD_COACH;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.CONFIRM_COACH;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;

public class SuperAdmin implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;

    private String firstNameCoach;
    private String lastNameCoach;
    private String chatIdCoach;
    private final String idSuperAdmin;
    private static Logger LOGGER = LogManager.getLogger(SuperAdmin.class);

    public SuperAdmin(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        idSuperAdmin = SuperAdminUtils.getInstance().getIdSuperAdminFromFileConfig();
    }

    @Override
    public void execute(Update update) {
        String command = getText(update);
        List<TelegramUser> listOfAdmin = userService.findUsersByRole("ADMIN");

        if (command.equals(ADD_COACH.getCommandName()) &&
                !getChatId(update).equals(idSuperAdmin) &&
                listOfAdmin.stream().noneMatch(admin -> (admin.getId().equals(getChatId(update))))) {
            firstNameCoach = getFirstName(update);
            lastNameCoach = getLastName(update);
            chatIdCoach = getChatId(update);
            userService.findByChatId(chatIdCoach).ifPresent(p -> {
                if (p.isEqualsRole("COACH")) {
                    sendMessageService.deleteMessage(chatIdCoach, getMessageId(update));
                } else {
                    sendMessageService.sendMessage(getChatId(update), "Ожидайте подтверждения...", null);
                    String message = "Подтвердите тренера <i>" + firstNameCoach + " " + lastNameCoach + "</i>";
                    InlineKeyboardMarkup board = builder().add("Подтвердить", "confirm_coach/ok/" + firstNameCoach + "/" + lastNameCoach)
                            .add("Отменить", "confirm_coach/no").create();
                    listOfAdmin.forEach(admin -> sendMessageService.sendMessage(admin.getId(), message, board));
                }
            });
        }
        if (update.hasCallbackQuery()) {
            command = Objects.requireNonNull(getCallbackQuery(update)).split("/")[0];
            if (command.equals(CONFIRM_COACH.getCommandName())) {
                String answer = Objects.requireNonNull(getCallbackQuery(update)).split("/")[1];
                if (answer.equals("ok")) {
                    userService.findByChatId(chatIdCoach).stream().findFirst()
                            .ifPresent(coach -> {
                                coach.setId(coach.getId());
                                coach.setFirstName(firstNameCoach);
                                coach.setLastName(lastNameCoach);
                                coach.setRole(userService.findRoleByName("COACH"));
                                userService.saveUser(coach);
                                listOfAdmin.forEach(admin -> sendMessageService.editMessage(admin.getId(), getMessageId(update), "Successful!", null));
                                sendMessageService.sendMessage(coach.getId(), "Вы добавлены тренером!", null);
                            });
                } else {
                    if (answer.equals("no")) {
                        listOfAdmin.forEach(admin -> sendMessageService.deleteMessage(admin.getId(), getMessageId(update)));
                        sendMessageService.sendMessage(chatIdCoach, "Отклонено!", null);
                    }
                }
            }
            if (!TelegramBot.getInstance().notifyMessageId.isEmpty()) {
                Message msg = Objects.requireNonNull(TelegramBot.getInstance().notifyMessageId.poll());
                sendMessageService.deleteMessage(getChatId(update), msg.getMessageId());
            }
        }
    }


}
