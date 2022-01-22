package com.enterprise.myshnev.telegrambot.scheduler.servises.user;

import com.enterprise.myshnev.telegrambot.scheduler.model.TelegramUser;

import java.util.List;
import java.util.Optional;

public interface UserService extends SentMessageService,StatisticService,RoleService {

    void saveUser(TelegramUser user);

    List<TelegramUser> findAll();

    Optional<TelegramUser> findByChatId(String chatId);


    void updateUser(TelegramUser telegramUser);

    List<TelegramUser> findUsersByRole(String role);


    List<TelegramUser> findAllByActive(boolean active);
}
