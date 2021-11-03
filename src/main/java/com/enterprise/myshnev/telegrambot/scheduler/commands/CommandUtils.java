package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.google.common.collect.ImmutableMap;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

public class CommandUtils {
    public final static Map<String, String> WEEK_FULL_NAME = ImmutableMap.<String, String>builder()
            .put("пн", "Понедельник")
            .put("вт", "Вторник")
            .put("ср", "Среда")
            .put("чт", "Четверг")
            .put("пт", "Пятница")
            .put("сб", "Суббота")
            .put("вс", "Воскресенье").build();
    public static final List<String> WEEK = List.of("пн", "вт", "ср", "чт", "пт", "сб", "вс");

    public static String getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        } else {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
    }

    public static String getFirstName(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getFirstName();
        } else {
            return update.getCallbackQuery().getFrom().getFirstName();
        }
    }

    public static String getLastName(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getLastName();
        } else {
            return update.getCallbackQuery().getFrom().getLastName();
        }
    }

    public static Integer getMessageId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getMessageId();
        } else {
            return update.getCallbackQuery().getMessage().getMessageId();
        }
    }

    public static String getText(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().getText() != null)
                return update.getMessage().getText();
            else
                return "none";
        } else {
            return update.getCallbackQuery().getMessage().getText();
        }
    }

    public static String getCallbackQuery(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        } else
            return null;
    }

}
