package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.model.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class UnknownCommand implements Command {
    private final SendMessageService sendMessageService;
    private final UserService userService;


    public UnknownCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        userService.findByChatId(getChatId(update)).stream()
             .findFirst().ifPresent(user -> {
            if (!user.getRole().getName().equals("COACH")) {
                sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
            } else {
                if (getText(update).startsWith("#")) {
                    userService.findAll().stream()
                            .filter(TelegramUser::isActive)
                            .forEach(u -> {
                                if (!u.getRole().getName().equals("COACH")) {
                                    String message;
                                    if (user.getLastName() == null) {
                                        message = "<strong>" + user.getFirstName() +
                                                " " + user.getLastName() + ":</strong>\n" + getText(update).replace('#', ' ');
                                    } else {
                                        message = "<strong>" + user.getFirstName() +
                                                " " + ":</strong>\n" + getText(update).replace('#', ' ');
                                    }
                                    sendMessageService.sendMessage(u.getId(), message, null);
                                }
                            });
                } else {
                    sendMessageService.deleteMessage(getChatId(update), getMessageId(update));
                }
            }
        });

    }
}
