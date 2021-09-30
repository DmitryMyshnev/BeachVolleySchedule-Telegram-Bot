package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.db.table.UserTable;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.EXIST;

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
        String chatId = update.getMessage().getChatId().toString();
        String firstName = update.getMessage().getFrom().getFirstName();
        String lastName = update.getMessage().getFrom().getLastName();
        TelegramUser user = new TelegramUser(chatId,firstName,lastName);
      if(!userService.save(user,new UserTable()).equals(EXIST.getStatus())){
          message = "Привет";
      }
      else {
          message = "Вы уже зарегистрированны";
      }
        sendMessageService.sendMessage(chatId,message);

    }
}
