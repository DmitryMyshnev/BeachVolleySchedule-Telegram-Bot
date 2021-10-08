package com.enterprise.myshnev.telegrambot.scheduler.repository.user;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UserRepositoryImpl implements UserRepository {

    @Override
    public String insertInto(String tableName,Object telegramUser,CrudDb table) {
      return table.insertIntoTable(tableName,telegramUser);
    }

    @Override
    public List<Object> findAll(String tableName,CrudDb table) {
        return table.findAll(tableName);
    }

    @Override
    public Optional<Object> findById(String tableName,String id,CrudDb table) {

        return table.findById(tableName,id);
    }

    @Override
    public List<Object> findBy(String tableName,String column,Object arg, CrudDb table) {
      return   table.findBy(tableName,column,arg);
    }

    @Override
    public Integer count(String tableName,CrudDb table) {
        return table.count(tableName);
    }

    @Override
    public String update(CrudDb table,String tableName,String chatId,String arg,String value) {
        return table.update(tableName,chatId,arg,value);
    }

    @Override
    public String delete(String taleName,String id,CrudDb table) {
        return table.delete(taleName,id);
    }
}
