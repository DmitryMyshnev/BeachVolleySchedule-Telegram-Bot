package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import com.google.common.collect.ImmutableMap;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;

public class CommandContainer {
    private final ImmutableMap<String,Command> commandMap;
    private final Command unknownCommand;

    public CommandContainer(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        StartCommand startCommand = new StartCommand(sendMessageService, userService);
        commandMap = ImmutableMap.<String,Command>builder()
                .put(START.getCommandName(),startCommand)
                .put(SUPER_ADMIN.getCommandName(),new SuperAdmin(sendMessageService))
                .put(SET_ADMIN.getCommandName(), new ServiceCommand(sendMessageService,userService))
                .put(GO.getCommandName(), new Go(sendMessageService,userService,workoutService))
                .put(ENJOY.getCommandName(), new AddUserToWorkoutButton(sendMessageService,userService,workoutService))
                .build();
        unknownCommand = new UnknownCommand();
        TimeOfNotification time = new TimeOfNotification(sendMessageService,userService,workoutService);
        time.startTimer();
    }
    public Command retrieveCommand(String commandIdentifier){
        return commandMap.getOrDefault(commandIdentifier,unknownCommand);
    }
}
