package com.enterprise.myshnev.telegrambot.scheduler.servises.user;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;

import java.util.List;
import java.util.Optional;

public interface UserService {
    String save(String tableName,Object telegramUser, CrudDb table);

    Optional<Object> findByChatId(String tableName,String chatId,CrudDb table);

    List<Object> findAll(String tableName,CrudDb table);

    List<Object> findBy(String tableName,String column,Object arg,CrudDb table);

    String update(CrudDb table,String tableName,String chatId, String arg,String value);

    String delete(String taleName,String chatId,CrudDb table);

    Integer count(String tableName,CrudDb table);
}
