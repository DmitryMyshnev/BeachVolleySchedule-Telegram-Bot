package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.keyboard.InlineKeyBoard.builder;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;
public class BackCommand implements Command{
    private final SendMessageService sendMessageService;
    private final UserService userService;

    public BackCommand(SendMessageService sendMessageService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    @Override
    public void execute(Update update) {
        String message;
        if(getCallbackQuery(update).equals(BACK_TO_MENU.getCommandName())){
            message = "Меню";
            sendMessageService.editMessage(getChatId(update),getMessageId(update),message,keyBoard());
        }
    }
    private InlineKeyboardMarkup keyBoard() {
        return builder()
                .add("Добавить тренера", ADD_COACH.getCommandName())
                .add("Создать тренировку", ADD_WORKOUT.getCommandName())
                .create();
    }
}
