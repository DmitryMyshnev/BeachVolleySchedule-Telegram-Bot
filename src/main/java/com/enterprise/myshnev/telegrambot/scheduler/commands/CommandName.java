package com.enterprise.myshnev.telegrambot.scheduler.commands;

public enum CommandName {
    START("/start"),
    SUPER_ADMIN("/superadmin"),
    SET_ADMIN("/iwanttobeadmin"),
    PASS_VALID("pass"),
    HELP("/help"),
    GO("/go"),
    ENJOY("enjoy"),
    VALIDATION("valid"),
    ADD_COACH("add_coach"),
    ADD_WORKOUT("add_workout"),
    CANCEL("cancel");
    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
