package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.CoachTable;
import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;

import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;

public class StartCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    String message;

    public StartCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        TelegramUser user = new TelegramUser(getChatId(update), getFirstName(update), getLastName(update));
        userService.findByChatId("Coach",getChatId(update), new CoachTable()).map(TelegramUser.class::cast).ifPresentOrElse(coach -> {
            message = "Привет, " + coach.getFirstName() + "! ";
            sendMessageService.sendMessage(coach.getChatId(), message);
        }, () -> {
            String stat = userService.save("Users",user, new UserTable());
            if (!stat.equals(EXIST.getStatus())) {
                List<TelegramUser> coach = userService.findAll("Coach",new CoachTable()).stream().map(us -> (TelegramUser) us).collect(Collectors.toList());
                message = "Привет, " + getFirstName(update) + "! Этот бот поможет тебе записываться на тренировки к тренеру: \n " +
                        coach.get(0).getFirstName() + " " +
                        coach.get(0).getLastName() +
                        "\n /help - посмотреть инструцию к боту\n" +
                        "/workout - посмотреть рассписание тренировок";
            } else {
                message = "Вы уже зарегистрированы";
            }
            sendMessageService.sendMessage(getChatId(update), message);
        });
    }
    public void time(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.format(System.currentTimeMillis());

    }



}
