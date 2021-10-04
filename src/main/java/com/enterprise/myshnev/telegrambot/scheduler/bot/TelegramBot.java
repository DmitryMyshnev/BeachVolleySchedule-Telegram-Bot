package com.enterprise.myshnev.telegrambot.scheduler.bot;

import com.enterprise.myshnev.telegrambot.scheduler.commands.CommandContainer;
import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageServiceImpl;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String BOT_USER_NAME = "exchange_CLI_bot";
    private static final String TOKEN = "2002904530:AAEVfsYTwAsbICA1pjuVtBYs-y9F1aCYZPA";
    private final CommandContainer commandContainer;
    private String commandIdentifier;
    @Value("${superAdmin.userId}")
    private String userId;


    @Autowired
    public TelegramBot(UserService userService) {
        commandContainer = new CommandContainer(new SendMessageServiceImpl(this), userService);
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_USER_NAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message;
        if (update.hasMessage() && update.getMessage().hasText()) {
           message = update.getMessage().getText().trim();
            if (message.startsWith("/")) {
                commandIdentifier = message.split(" ")[0].toLowerCase();
            } else {
                commandIdentifier = message.split(":")[0].toLowerCase().trim();
            }
        }
        if (update.hasCallbackQuery()) {
            message = getText(update);
           commandIdentifier = getCallbackQuery(update);
        }
        if(update.hasMessage() && update.getMessage().hasContact()){
            System.out.println();
        }
        if(commandIdentifier != null){
            commandContainer.retrieveCommand(commandIdentifier).execute(update);
        }
    }
}
