package com.enterprise.myshnev.telegrambot.scheduler.db;

public enum CommandQuery {
    CREATE_TABLE("CREATE TABLE IF NOT EXISTS '%s' (%s, PRIMARY KEY(%s));"),
    INSERT_INTO("INSERT INTO '%s' (%s) VALUES(%s);"),
    SELECT_WHERE("SELECT * FROM '%s' WHERE %s = '%s';"),
    UPDATE("UPDATE '%s' SET %s=%s WHERE %s = '%s';"),
    SELECT_ALL("SELECT * FROM '%s'"),
    DELETE("DELETE FROM '%s' WHERE %s='%s'"),
    DELETE_ALL("DELETE FROM '%s'"),
    COUNT("SELECT count(*) AS total FROM '%s'");
    private final String query;

    CommandQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
