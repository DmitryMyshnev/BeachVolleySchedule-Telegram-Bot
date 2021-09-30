package com.enterprise.myshnev.telegrambot.scheduler.servises.workout;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;

import java.util.List;
import java.util.Optional;

public interface WorkoutService {
    void save(Object workout, CrudDb table);

    Optional<Object> findByChatId(String chatId, CrudDb table);

    List<Object> findAll(CrudDb table);

    String update(CrudDb table,String chatId, String arg,String value);

    String delete(String chatId,CrudDb table);
}
