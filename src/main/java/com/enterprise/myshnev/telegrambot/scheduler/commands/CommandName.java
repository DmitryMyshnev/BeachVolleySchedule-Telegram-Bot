package com.enterprise.myshnev.telegrambot.scheduler.commands;

public enum CommandName {
    START("/start"),
    STOP("/stop"),
    ADMIN("/admin"),
    STATISTIC("/stat"),
    WORKOUTS("/workouts"),
    HELP("/help"),
    ENJOY("enjoy"),
    CONFIRM("confirm"),
    EDIT_WORKOUT("editWorkout"),
    CHANGE_WORKOUT("changeWorkout"),
    ADD_COACH("/iamcoach"),
    CONFIRM_COACH("confirm_coach"),
    ADD_WORKOUT("add_workout"),
    ADD("add"),
    CANCEL_WORKOUT("cancel_workout"),
    GET_DATA_BASE("/db"),
    SET_TIME_NOTIFY("time"),
    CLEAR_MESSAGE("/clear");
    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
