package com.enterprise.myshnev.telegrambot.scheduler.servises.user;

import com.enterprise.myshnev.telegrambot.scheduler.model.Role;
import com.enterprise.myshnev.telegrambot.scheduler.model.SentMessages;
import com.enterprise.myshnev.telegrambot.scheduler.model.Statistic;
import com.enterprise.myshnev.telegrambot.scheduler.model.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.RoleRepository;
import com.enterprise.myshnev.telegrambot.scheduler.repository.user.SentMessagesRepository;
import com.enterprise.myshnev.telegrambot.scheduler.repository.user.StatisticRepository;
import com.enterprise.myshnev.telegrambot.scheduler.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  SentMessagesRepository sentMessagesRepository;
    @Autowired
    private  StatisticRepository statisticRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void saveUser(TelegramUser user) {
        userRepository.save(user);
    }

    @Override
    public List<TelegramUser> findAll() {
        return userRepository.findAll();
    }


    @Override
    public Optional<SentMessages> findByMessageId(Integer messageId) {
        return sentMessagesRepository.findByMessageId(messageId);
    }

    @Override
    public void saveStatistic(Statistic stat) {
        statisticRepository.save(stat);
    }

    @Override
    public void saveMessage(SentMessages sentMessages) {
        sentMessagesRepository.save(sentMessages);
    }

    @Override
    public void deleteSentMessage(SentMessages sentMessages) {
        sentMessagesRepository.delete(sentMessages);
    }

    @Override
    public List<SentMessages> findByWorkoutId(Long workoutId) {
        return sentMessagesRepository.findByWorkoutId(workoutId);
    }

    @Override
    public Optional<TelegramUser> findByChatId(String chatId) {
        return userRepository.findById(chatId);
    }


    @Override
    public void updateUser(TelegramUser telegramUser) {
        userRepository.save(telegramUser);
    }

    @Override
    public List<Statistic> findAllStatistic() {
        return statisticRepository.findAll();
    }

    @Override
    public List<TelegramUser> findUsersByRole(String role) {
        Long id = roleRepository.findByName(role).getId();
        return userRepository.findByRoleId(id);
    }

    @Override
    public Role findRoleByName(String role) {
        return roleRepository.findByName(role);
    }

    @Override
    public Optional<SentMessages> findByUserIdAndWorkoutId(String chatId,Long workoutId) {
        return sentMessagesRepository.findByUserIdAndWorkoutId(chatId,workoutId);
    }

    @Override
    public List<SentMessages> findMessageId(Long workoutId) {
        return sentMessagesRepository.findByWorkoutId(workoutId);
    }

    @Override
    public Optional<SentMessages> findSentMessage(String chatId, Long workoutId) {
        return sentMessagesRepository.findSentMessage(chatId,workoutId);
    }

    @Override
    public List<TelegramUser> findAllByActive(boolean active) {
        return userRepository.findByActive(active);
    }
}
