package com.enterprise.myshnev.telegrambot.scheduler.servises.workout;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.workout.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class WorkoutServiceImpl implements WorkoutService{
    private WorkoutRepository workoutRepository;
@Autowired
    public WorkoutServiceImpl(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @Override
    public void save(Object workout, CrudDb table) {

    }

    @Override
    public Optional<Object> findByChatId(String chatId, CrudDb table) {
        return Optional.empty();
    }

    @Override
    public List<Object> findAll(CrudDb table) {
        return null;
    }

    @Override
    public String update(CrudDb table, String chatId, String arg, String value) {
        return null;
    }

    @Override
    public String delete(String chatId, CrudDb table) {
        return null;
    }
}
