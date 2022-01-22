package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.getChatId;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.getMessageId;

public class StopCommand  implements Command{
    private final SendMessageService sendMessageService;
    private final UserService userService;


    public StopCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;

    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(getChatId(update)).ifPresentOrElse(user->{
            if(user.isEqualsRole("COACH") || user.isEqualsRole("ADMIN")) {
                sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
            }else {
                user.setActive(false);
                userService.updateUser(user);
                String  message = "❌ Уведомления отключены! \n Вам не будут приходить сообщения о начале записи на тренировки.\n" +
                        "/start - для включения уведомлений.";
                sendMessageService.sendMessage(getChatId(update),message,null);
            }
        },()-> sendMessageService.deleteMessage(getChatId(update), getMessageId(update)));
    }
}
