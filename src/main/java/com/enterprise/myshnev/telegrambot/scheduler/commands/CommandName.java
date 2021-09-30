package com.enterprise.myshnev.telegrambot.scheduler.commands;

public enum CommandName {
    START("/start"),
    SUPER_ADMIN("/superadmin"),
    HELP("/help"),
    NO("/no"),
    STAT("/stat");
    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
