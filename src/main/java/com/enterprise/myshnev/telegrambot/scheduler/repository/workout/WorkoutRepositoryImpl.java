package com.enterprise.myshnev.telegrambot.scheduler.repository.workout;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class WorkoutRepositoryImpl implements WorkoutRepository{
    @Override
    public void insertInto(Object workout, CrudDb table) {

    }

    @Override
    public List<Object> findAll(CrudDb table) {
        return null;
    }

    @Override
    public Optional<Object> findById(String id, CrudDb table) {
        return Optional.empty();
    }

    @Override
    public String update(CrudDb table, String id, String arg, String value) {
        return null;
    }

    @Override
    public String delete(String id, CrudDb table) {
        return null;
    }
}
