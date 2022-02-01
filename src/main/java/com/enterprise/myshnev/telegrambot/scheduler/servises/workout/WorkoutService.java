package com.enterprise.myshnev.telegrambot.scheduler.servises.workout;

import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;

import java.util.List;
import java.util.Optional;

public interface WorkoutService extends NewWorkoutService {

    List<Workout> findAllWorkout();

    void saveWorkout(Workout workout);

    void updateWorkout(Workout workout);

    void deleteWorkout(Workout workout);

    Workout findWorkoutByTime(String deyOfWeek, String timeOfWorkout);


    Optional<Workout> findWorkout(Long id);
}
