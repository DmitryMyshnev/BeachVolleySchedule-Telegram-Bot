package com.enterprise.myshnev.telegrambot.scheduler.servises.user;

import com.enterprise.myshnev.telegrambot.scheduler.model.SentMessages;

import java.util.List;
import java.util.Optional;

public interface SentMessageService {

    Optional<SentMessages> findByMessageId(Integer messageId);

    Optional<SentMessages> findByUserIdAndWorkoutId(String chatId,Long workoutId);

    void saveMessage(SentMessages sentMessages);

    void deleteSentMessage(SentMessages sentMessages);

    List<SentMessages> findByWorkoutId(Long workoutId);

    List<SentMessages> findSentMessages(Long workoutId);

    Optional<SentMessages> findSentMessage(String chatId, Long workoutId);
}
