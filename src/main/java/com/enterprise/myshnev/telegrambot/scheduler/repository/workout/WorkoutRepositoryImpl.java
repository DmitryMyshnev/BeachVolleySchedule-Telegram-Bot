package com.enterprise.myshnev.telegrambot.scheduler.repository.workout;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.MediaSize;
import java.util.List;
import java.util.Optional;
@Service
public class WorkoutRepositoryImpl implements WorkoutRepository{
    @Override
    public String addTable(String name,CrudDb newTable) {
        return newTable.addTable(name);
    }

    @Override
    public void insertInto(String tableName,Object workout, CrudDb table) {
        table.insertIntoTable(tableName,workout);
    }

    @Override
    public List<Object> findAll(String tableName,CrudDb table) {
        return table.findAll(tableName);
    }

    @Override
    public Optional<Object> findById(String tableName,String id, CrudDb table) {
        return table.findById(tableName,id);
    }

    @Override
    public List<Object> findBy(String tableName, String column,Object arg, CrudDb table) {
        return table.findBy(tableName,column,arg);
    }

    @Override
    public String update(CrudDb table, String tableName,String chatId, String arg, String value) {
        return table.update(tableName,chatId,arg,value);
    }

    @Override
    public String delete(String tableName,String id, CrudDb table) {
        return table.delete(tableName,id);
    }

    @Override
    public Integer count(String tableName,CrudDb table) {
        return table.count(tableName);
    }
}
