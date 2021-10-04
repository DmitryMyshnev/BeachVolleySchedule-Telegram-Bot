package com.enterprise.myshnev.telegrambot.scheduler.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandUtils {
    public static String getChatId(Update update){
        if(update.hasMessage()){
            return update.getMessage().getChatId().toString();
        }else {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
    }
public static String getFirstName(Update update){
    if(update.hasMessage()){
        return update.getMessage().getFrom().getFirstName();
    }else {
        return update.getCallbackQuery().getFrom().getFirstName();
    }
}
    public static String getLastName(Update update){
        if(update.hasMessage()){
            return update.getMessage().getFrom().getLastName();
        }else {
            return update.getCallbackQuery().getFrom().getLastName();
        }
    }
    public static Integer getMessageId(Update update){
        if(update.hasMessage()){
            return update.getMessage().getMessageId();
        }else {
            return update.getCallbackQuery().getMessage().getMessageId();
        }
    }
    public static String getText(Update update){
        if(update.hasMessage()){
            return update.getMessage().getText();
        }else {
            return update.getCallbackQuery().getMessage().getText();
        }
    }
    public static String getCallbackQuery(Update update){
        return  update.getCallbackQuery().getData();
    }

}
