package com.enterprise.myshnev.telegrambot.scheduler.servises;

import com.enterprise.myshnev.telegrambot.scheduler.bot.TelegramBot;
import com.enterprise.myshnev.telegrambot.scheduler.commands.CommandContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.getCallbackQuery;


public class ReceiveMessage implements Runnable{
    public static Logger LOGGER = LogManager.getLogger(ReceiveMessage.class);
    private final TelegramBot bot;
    private final CommandContainer commandContainer;

    public ReceiveMessage(TelegramBot bot,CommandContainer commandContainer) {
     this.bot = bot;
     this.commandContainer = commandContainer;
    }

    @Override
    public void run() {
       while (true){
           if(!bot.receiveQueue.isEmpty()) {
               executeCommand(Objects.requireNonNull(bot.receiveQueue.poll()));
           }
       }
    }
    private void executeCommand(Update update){
        String message;
        String commandIdentifier = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            message = update.getMessage().getText().trim();
            if (message.startsWith("/")) {
                commandIdentifier = message.split(" ")[0].toLowerCase();
            } else {
                commandIdentifier = message.split("/")[0].toLowerCase().trim();
            }
        }
        if (update.hasCallbackQuery()) {
            commandIdentifier = Objects.requireNonNull(getCallbackQuery(update)).split("/")[0];
        }
       /* if(getChatId(update).equals(superAdmin)){
             commandIdentifier = getText(update).split("/")[0];
         }*/
        commandContainer.retrieveCommand(commandIdentifier).execute(update);

    }
}
