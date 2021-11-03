package com.enterprise.myshnev.telegrambot.scheduler.db.table;

public enum Tables {
    ADMIN("Admin"),
    COACH("Coach"),
    MESSAGE_ID("MessageId"),
    USERS("Users"),
    WORKOUT("Workout"),
    STATISTIC("Statistic");
    private final String tables;

    Tables(String tables) {
        this.tables = tables;
    }

    public String getTableName() {
        return tables;
    }
}
