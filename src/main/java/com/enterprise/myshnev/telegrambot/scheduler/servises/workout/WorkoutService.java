package com.enterprise.myshnev.telegrambot.scheduler.servises.workout;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;

import java.util.List;
import java.util.Optional;

public interface WorkoutService {
    String addTable(String name, CrudDb newTable);

    void save(String tableName,Object workout, CrudDb table);

    Optional<Object> findByChatId(String tableName,String chatId, CrudDb table);

    List<Object> findBy(String tableName,String column,Object arg, CrudDb table);

    List<Object> findAll(String tableName,CrudDb table);

    String update(CrudDb table,String tableName,String chatId, String arg,String value);

    String delete(String tableName,String chatId,CrudDb table);

    Integer count(String tableName,CrudDb table);
}
