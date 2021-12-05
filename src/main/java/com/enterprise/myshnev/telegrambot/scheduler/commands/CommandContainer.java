package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import com.enterprise.myshnev.telegrambot.scheduler.timer.TimerOfNotification;
import com.google.common.collect.ImmutableMap;

import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandName.*;

public class CommandContainer {
    private final ImmutableMap<String,Command> commandMap;
    private final Command unknownCommand;

    public CommandContainer(SendMessageService sendMessageService, UserService userService, WorkoutService workoutService) {
        StartCommand startCommand = new StartCommand(sendMessageService, userService,workoutService);
        AddWorkoutCommand addWorkoutCommand = new AddWorkoutCommand(sendMessageService,userService,workoutService);
        SuperAdmin superAdmin = new SuperAdmin(sendMessageService,userService,workoutService);
        commandMap = ImmutableMap.<String,Command>builder()
                .put(START.getCommandName(),startCommand)
                .put(STOP.getCommandName(),new StopCommand(sendMessageService,userService))
                .put(HELP.getCommandName(), new HelpCommand(sendMessageService,userService))
                .put(ADMIN.getCommandName(),superAdmin)
                .put(ADD_COACH.getCommandName(), superAdmin)
                .put(CONFIRM_COACH.getCommandName(), superAdmin)
                .put(ENJOY.getCommandName(), new AddUserToWorkout(sendMessageService,userService,workoutService))
                .put(WORKOUTS.getCommandName(),new  ScheduleCommand(sendMessageService,userService,workoutService))
                .put(CANCEL_WORKOUT.getCommandName(), new CancelWorkoutCommand(sendMessageService,userService,workoutService))
                .put(CONFIRM.getCommandName(), new ConfirmCommand(sendMessageService,userService,workoutService))
                .put(EDIT_WORKOUT.getCommandName(), new EditCommand(sendMessageService,workoutService))
                .put(CHANGE_WORKOUT.getCommandName(), new EditWorkoutCommand(sendMessageService,workoutService))
                .put(ADD_WORKOUT.getCommandName(), addWorkoutCommand)
                .put(ADD.getCommandName(), addWorkoutCommand)
                .put(STATISTIC.getCommandName(),new StatInfoCommand(sendMessageService,userService))
                .put(GET_DATA_BASE.getCommandName(), new GetDbCommand(sendMessageService))
                .put(SET_TIME_NOTIFY.getCommandName(),new SetTimeOfNotifyCommand(sendMessageService))
                .build();
        unknownCommand = new UnknownCommand(sendMessageService,userService);
        TimerOfNotification time = new TimerOfNotification(sendMessageService,userService,workoutService);
        time.startTimer();
    }
    public Command retrieveCommand(String commandIdentifier){
        return commandMap.getOrDefault(commandIdentifier,unknownCommand);
    }
}
