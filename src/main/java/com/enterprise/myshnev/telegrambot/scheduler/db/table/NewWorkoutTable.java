package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;

import java.util.List;
import java.util.Optional;

public class NewWorkoutTable implements CrudDb<NewWorkout> {
    @Override
    public String insertIntoTable(NewWorkout type) {
        return null;
    }

    @Override
    public List<NewWorkout> findAll() {
        return null;
    }

    @Override
    public Optional<NewWorkout> findById(String id) {
        return Optional.empty();
    }

    @Override
    public String update(String id, String arg, String value) {
        return null;
    }

    @Override
    public String delete(String id) {
        return null;
    }

    @Override
    public Integer count() {
        return null;
    }
}
