package com.enterprise.myshnev.telegrambot.scheduler.servises.user;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;

import java.util.List;
import java.util.Optional;

public interface UserService {
    String save(Object telegramUser, CrudDb table);

    Optional<Object> findByChatId(String chatId,CrudDb table);

    List<Object> findAll(CrudDb table);

    String update(CrudDb table,String chatId, String arg,String value);

    String delete(String chatId,CrudDb table);

    Integer count(CrudDb table);
}
