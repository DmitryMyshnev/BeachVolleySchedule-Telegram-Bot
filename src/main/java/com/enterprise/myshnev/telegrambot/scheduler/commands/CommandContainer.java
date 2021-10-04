package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.google.common.collect.ImmutableMap;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;

public class CommandContainer {
    private final ImmutableMap<String,Command> commandMap;
    private final Command unknownCommand;

    public CommandContainer(SendMessageService sendMessageService, UserService userService) {
        StartCommand startCommand = new StartCommand(sendMessageService, userService);
        commandMap = ImmutableMap.<String,Command>builder()
                .put(START.getCommandName(),startCommand)
                .put(SUPER_ADMIN.getCommandName(),new SuperAdmin(sendMessageService))
                .put(SET_ADMIN.getCommandName(), new ServiceCommand(sendMessageService,userService))
                .put(GO.getCommandName(), new Go(sendMessageService,userService))
                .put(PASS_VALID.getCommandName(), new  PassValidation(sendMessageService,userService))
                .put(VALIDATION.getCommandName(), new ValidationCommand(sendMessageService,userService))
                .put(BACK_TO_MENU.getCommandName(), new BackCommand(sendMessageService,userService))
                .build();
        unknownCommand = new UnknownCommand();
    }
    public Command retrieveCommand(String commandIdentifier){
        return commandMap.getOrDefault(commandIdentifier,unknownCommand);
    }
}
