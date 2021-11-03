package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.USERS;

public class UnknownCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;
    private final UserTable userTable;

    public UnknownCommand(SendMessageService sendMessageService,UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
        userTable = new UserTable();
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(USERS.getTableName(),getChatId(update),userTable).stream().
                map(u->(TelegramUser)u).findFirst().ifPresent(user->{
                    if(!user.isCoach()){
                        sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
                    }else {
                        if(getText(update).startsWith("#")){
                            userService.findAll(USERS.getTableName(),userTable).stream()
                                    .map(m->(TelegramUser)m)
                                    .forEach(u->{
                                        if(!u.isCoach()) {
                                            String message = "<strong>" + user.getFirstName() + " " + user.getLastName() + ":</strong>\n" + getText(update).replace('#',' ');
                                            sendMessageService.sendMessage(u.getChatId(), message, null);
                                        }
                                    });
                        }else {
                            sendMessageService.deleteWorkoutMessage(getChatId(update), getMessageId(update));
                        }
                    }
        });

    }
}
