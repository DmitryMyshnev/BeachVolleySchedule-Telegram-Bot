package com.enterprise.myshnev.telegrambot.scheduler.repository.workout;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository {
    String addTable(String name,CrudDb newTable);

    void insertInto(String tableName,Object workout, CrudDb table);

    List<Object> findAll(String tableName,CrudDb table);

    Optional<Object> findById(String tableName,String id, CrudDb table);

    List<Object> findBy(String tableName,String column,Object arg, CrudDb table);

    String update(CrudDb table,String tableName,String chatId,String arg,String value);

    String delete(String tableName,String id,CrudDb table);

    Integer count(String tableName,CrudDb table);
}
