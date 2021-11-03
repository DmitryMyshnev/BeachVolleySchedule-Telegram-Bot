package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.table.Tables.*;

public class StopCommand  implements Command{
    private final SendMessageService sendMessageService;
    private final UserService userService;

    public StopCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(COACH.getTableName(), getChatId(update),new UserTable()).ifPresentOrElse(p->{
            sendMessageService.deleteMessage(getChatId(update),getMessageId(update));
        },()->{
            userService.update(new UserTable(), USERS.getTableName(), getChatId(update),"active","0");
            String  message = "❌ Уведомления отключены! \n Вам не будут приходить сообщения о начале записи на тренировки.\n" +
                    "/start - для включения уведомлений.";
            sendMessageService.sendMessage(getChatId(update),message,null);
        });
    }
}
