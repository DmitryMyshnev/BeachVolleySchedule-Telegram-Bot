package com.enterprise.myshnev.telegrambot.scheduler.repository.workout;

import com.enterprise.myshnev.telegrambot.scheduler.model.NewWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewWorkoutRepository extends JpaRepository<NewWorkout, Long> {
    Optional<NewWorkout> findByChatId(String chatId);

    Optional<NewWorkout> findByChatIdAndWorkoutId(String chatId, Long workoutId);

    @Query(value = "SELECT * FROM New_workout nw WHERE nw.workout_id=:id",
            nativeQuery = true)
    List<NewWorkout> findByWorkoutId(@Param("id") Long id);
}
