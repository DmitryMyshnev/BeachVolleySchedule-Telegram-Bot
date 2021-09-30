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
    public Optional<Object> findByChatId(String chatId,CrudDb table) {
        return userRepository.findById(chatId,table);
    }

    @Override
    public String save(Object telegramUser, CrudDb table) {
       return userRepository.insertInto(telegramUser,table);
    }

    @Override
    public List<Object> findAll(CrudDb table) {
        return userRepository.findAll(table);
    }

    @Override
    public String update(CrudDb table,String chatId,String arg,String value) {
        return userRepository.update(table,chatId,arg,value);
    }

    @Override
    public String delete(String chatId,CrudDb table) {
        return userRepository.delete(chatId,table);
    }
}
