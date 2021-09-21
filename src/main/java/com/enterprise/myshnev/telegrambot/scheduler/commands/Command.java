package com.enterprise.myshnev.telegrambot.scheduler.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    void execute(Update update);
}
