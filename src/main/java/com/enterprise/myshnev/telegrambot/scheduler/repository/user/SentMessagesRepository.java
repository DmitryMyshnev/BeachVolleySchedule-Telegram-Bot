package com.enterprise.myshnev.telegrambot.scheduler.repository.user;

import com.enterprise.myshnev.telegrambot.scheduler.model.SentMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SentMessagesRepository extends JpaRepository<SentMessages, Integer> {

    Optional<SentMessages> findByUserIdAndWorkoutId(String chatId,Long workoutId);

    @Query(value = "SELECT * FROM Sent_messages m WHERE m.workout_id =:id",
            nativeQuery = true)
    List<SentMessages> findByWorkoutId(@Param("id") Long workoutId);

    @Query(value = "SELECT * FROM Sent_messages m WHERE  m.user_id =:chat and m.workout_id =:workout",
            nativeQuery = true)
    Optional<SentMessages> findSentMessage(@Param("chat") String chatId,
                                           @Param("workout") Long workoutId);

    Optional<SentMessages> findByMessageId(Integer messageId);

}
