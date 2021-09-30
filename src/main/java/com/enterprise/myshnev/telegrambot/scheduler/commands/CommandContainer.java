package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageServiceImpl;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.google.common.collect.ImmutableMap;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;

public class CommandContainer {
    private final ImmutableMap<String,Command> commandMap;
    private final Command unknownCommand;

    public CommandContainer(SendMessageService sendMessageService, UserService userService) {
        commandMap = ImmutableMap.<String,Command>builder()
                .put(START.getCommandName(),new StartCommand(sendMessageService, userService))
                .put(SUPER_ADMIN.getCommandName(),new SuperAdmin(sendMessageService))
                .build();
        unknownCommand = new UnknownCommand();
    }
    public Command retrieveCommand(String commandIdentifier){
        return commandMap.getOrDefault(commandIdentifier,unknownCommand);
    }
}
