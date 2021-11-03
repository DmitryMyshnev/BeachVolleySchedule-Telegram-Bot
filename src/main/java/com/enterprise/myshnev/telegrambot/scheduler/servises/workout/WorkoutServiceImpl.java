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
    public String addTable(String name, CrudDb newTable) {
        return workoutRepository.addTable(name,newTable);
    }

    @Override
    public void save(String tableName,Object workout, CrudDb table) {
       workoutRepository.insertInto(tableName,workout,table);
    }

    @Override
    public Optional<Object> findByChatId(String tableName,String chatId, CrudDb table) {
        return workoutRepository.findById(tableName,chatId,table);
    }

    @Override
    public List<Object> findBy(String tableName,String column,Object arg, CrudDb table) {
        return workoutRepository.findBy(tableName,column,arg,table);
    }

    @Override
    public Integer count(String tableName,CrudDb table) {
        return workoutRepository.count(tableName,table);
    }

    @Override
    public List<Object> findAll(String tableName,CrudDb table) {
        return workoutRepository.findAll(tableName,table);
    }

    @Override
    public String update(CrudDb table,String tableName, String chatId, String arg, String value) {
        return workoutRepository.update(table,tableName,chatId,arg,value);
    }

    @Override
    public String delete(String tableName,String chatId, CrudDb table) {
        return workoutRepository.delete(tableName,chatId,table);
    }

    @Override
    public void dropTable(String tableName, CrudDb table) {
        workoutRepository.dropTable(tableName,table);
    }
}
