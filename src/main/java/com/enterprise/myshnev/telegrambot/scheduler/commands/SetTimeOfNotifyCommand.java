package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class SetTimeOfNotifyCommand implements Command{
    private final String superAdminId;
    private final SendMessageService sendMessageService;

    public SetTimeOfNotifyCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;
        this.superAdminId = SuperAdminUtils.getInstance().getIdSuperAdminFromFileConfig();
    }
    @Override
    public void execute(Update update) {
        if(superAdminId.equals(getChatId(update))){
            String time = getText(update).split("/")[1];
            Pattern pattern = Pattern.compile("^(2[0-3]|1\\d|\\d)(:)([0-5][0-9])(\\s*)$");
            Matcher matcher = pattern.matcher(time);
           if(matcher.find()) {
               SuperAdminUtils.TIME_OF_NOTIFICATION = time;
               sendMessageService.sendMessage(getChatId(update), "Время оповещения: " + time, null);
           }else {
               sendMessageService.sendMessage(getChatId(update),"Не верно указано время",null);
           }
        }else {
            sendMessageService.deleteMessage(getChatId(update),getMessageId(update));
        }
    }
}
