package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

import java.io.*;

public class GetDbCommand implements Command {
    private final String PATH_DB = System.getProperty("user.dir") + File.separator + "user.db";
    private final String superAdminId;
    private final SendMessageService  sendMessageService;

    public GetDbCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
        superAdminId = SuperAdminUtils.getIdSuperAdminFromFileConfig();
    }

    @Override
    public void execute(Update update) {
        if (getChatId(update).equals(superAdminId)) {
            sendMessageService.sendDocument(getChatId(update), new File(PATH_DB) );
        }else {
            sendMessageService.deleteMessage(getChatId(update),getMessageId(update));
        }
    }
}
