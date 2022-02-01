package com.enterprise.myshnev.telegrambot.scheduler.repository.workout;

import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutRepository  extends JpaRepository<Workout,Long> {

    Workout findByDayOfWeekAndTime(String dayOfWeek, String timeWorkout);
}
