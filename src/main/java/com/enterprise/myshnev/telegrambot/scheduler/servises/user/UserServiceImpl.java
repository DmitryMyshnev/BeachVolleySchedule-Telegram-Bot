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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SentMessagesRepository sentMessagesRepository;
    private final StatisticRepository statisticRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, SentMessagesRepository sentMessagesRepository, StatisticRepository statisticRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.sentMessagesRepository = sentMessagesRepository;
        this.statisticRepository = statisticRepository;
        this.roleRepository = roleRepository;
    }

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

    @CacheEvict(cacheNames = "sent_messages",allEntries = true)
    @Override
    public void saveMessage(SentMessages sentMessages) {
        sentMessagesRepository.save(sentMessages);
    }

    @CacheEvict(cacheNames = "sent_messages",allEntries = true)
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
    public Optional<SentMessages> findByUserIdAndWorkoutId(String chatId, Long workoutId) {
        return sentMessagesRepository.findByUserIdAndWorkoutId(chatId, workoutId);
    }

    @Cacheable("sent_messages")
    @Override
    public List<SentMessages> findSentMessages(Long workoutId) {
        return sentMessagesRepository.findByWorkoutId(workoutId);
    }

    @Override
    public Optional<SentMessages> findSentMessage(String chatId, Long workoutId) {
        return sentMessagesRepository.findSentMessage(chatId, workoutId);
    }

    @Override
    public List<TelegramUser> findAllByActive(boolean active) {
        return userRepository.findByActive(active);
    }

    @Override
    public List<SentMessages> findAllSentMessage() {
        return sentMessagesRepository.findAll();
    }
}
