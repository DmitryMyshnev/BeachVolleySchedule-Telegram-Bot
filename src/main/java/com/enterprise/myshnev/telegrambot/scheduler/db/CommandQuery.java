package com.enterprise.myshnev.telegrambot.scheduler.db;

public enum CommandQuery {
    INSERT_INTO("INSERT INTO %s (%s) VALUES(%s);"),
    SELECT_FROM("SELECT * FROM %s WHERE %s = '%s';"),
    UPDATE("UPDATE %s SET %s=%s WHERE %s = '%s';"),
    SELECT_ALL("SELECT * FROM %s"),
    DELETE("DELETE FROM %s WHERE %s='%s'");
    private String query;

    CommandQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
