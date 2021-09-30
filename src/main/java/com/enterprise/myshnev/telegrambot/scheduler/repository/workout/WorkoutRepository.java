package com.enterprise.myshnev.telegrambot.scheduler.repository.workout;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository {
    void insertInto(Object workout, CrudDb table);

    List<Object> findAll(CrudDb table);

    Optional<Object> findById(String id, CrudDb table);

    String update(CrudDb table,String id,String arg,String value);

    String delete(String id,CrudDb table);

}
