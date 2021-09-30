package com.enterprise.myshnev.telegrambot.scheduler.repository.user;

import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    String insertInto(Object telegramUser, CrudDb table);

    List<Object> findAll(CrudDb table);

    Optional<Object> findById(String id,CrudDb table);

    String update(CrudDb table,String id,String arg,String value);

    String delete(String id,CrudDb table);
}
