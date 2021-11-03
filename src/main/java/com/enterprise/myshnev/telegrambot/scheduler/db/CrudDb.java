package com.enterprise.myshnev.telegrambot.scheduler.db;

import java.util.List;
import java.util.Optional;

public interface CrudDb<T> {
    String addTable(String tableName);

    String insertIntoTable(String tableName,T type);

    List<T> findAll(String tableName);

    Optional<T> findById(String tableName,String id);

   List<T> findBy(String tableName, String column, Object arg);

    String update(String tableName,String chatId,String arg, String value);

    String delete(String tableName,String id);

    Integer count(String tableName);

    void dropTable(String tableName);
}
