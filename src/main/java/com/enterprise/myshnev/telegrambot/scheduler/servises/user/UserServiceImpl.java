package com.enterprise.myshnev.telegrambot.scheduler.servises.user;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
@Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;//new UserRepositoryImpl();
    }

    @Override
    public Optional<Object> findByChatId(String tableName,String chatId,CrudDb table) {
        return userRepository.findById(tableName,chatId,table);
    }

    @Override
    public List<Object> findBy(String tableName,String column,Object arg, CrudDb table) {
        return userRepository.findBy(tableName,column,arg,table);
    }

    @Override
    public String save(String tableName,Object telegramUser, CrudDb table) {
       return userRepository.insertInto(tableName,telegramUser,table);
    }

    @Override
    public List<Object> findAll(String tableName,CrudDb table) {
        return userRepository.findAll(tableName,table);
    }

    @Override
    public String update(CrudDb table,String tableName,String chatId,String arg,String value) {
        return userRepository.update(table,tableName,chatId,arg,value);
    }

    @Override
    public String delete(String taleName,String chatId,CrudDb table) {
        return userRepository.delete(taleName,chatId,table);
    }

    @Override
    public Integer count(String tableName,CrudDb table) {
        return userRepository.count(tableName,table);
    }
}
