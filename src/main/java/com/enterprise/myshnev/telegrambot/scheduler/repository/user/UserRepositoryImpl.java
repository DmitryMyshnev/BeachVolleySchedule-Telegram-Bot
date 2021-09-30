package com.enterprise.myshnev.telegrambot.scheduler.repository.user;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UserRepositoryImpl implements UserRepository {

    @Override
    public String insertInto(Object telegramUser,CrudDb table) {
      return table.insertIntoTable(telegramUser);
    }

    @Override
    public List<Object> findAll(CrudDb table) {
        return table.findAll();
    }

    @Override
    public Optional<Object> findById(String id,CrudDb table) {

        return table.findById(id);
    }

    @Override
    public String update(CrudDb table,String id,String arg,String value) {
        return table.update(id,arg,value);
    }

    @Override
    public String delete(String id,CrudDb table) {
        return table.delete(id);
    }
}
