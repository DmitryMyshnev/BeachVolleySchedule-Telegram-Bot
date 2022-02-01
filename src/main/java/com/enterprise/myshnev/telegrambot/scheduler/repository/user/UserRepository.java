package com.enterprise.myshnev.telegrambot.scheduler.repository.user;

import com.enterprise.myshnev.telegrambot.scheduler.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<TelegramUser,String>{

    List<TelegramUser> findByRoleId(Long id);

    List<TelegramUser> findByActive(boolean active);
}
