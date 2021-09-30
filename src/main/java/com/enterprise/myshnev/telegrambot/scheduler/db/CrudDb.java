package com.enterprise.myshnev.telegrambot.scheduler.db;

import java.util.List;
import java.util.Optional;

public interface CrudDb<T> {
    String insertIntoTable(T type);

    List<T> findAll();

    Optional<T> findById(String id);

    String update(String id,String arg, String value);

    String delete(String id);
}
