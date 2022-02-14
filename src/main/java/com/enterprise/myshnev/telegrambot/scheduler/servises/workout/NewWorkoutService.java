package com.enterprise.myshnev.telegrambot.scheduler.servises.workout;

import com.enterprise.myshnev.telegrambot.scheduler.model.NewWorkout;

import java.util.List;
import java.util.Optional;

public interface NewWorkoutService {


    Optional<NewWorkout> findJoinedUser(String chatId, Long workout_D);

    void delete(NewWorkout user);

    List<NewWorkout> findAllJoinedUsers(Long id);

    void updateNewWorkout(NewWorkout newWorkout);

    void saveNewWorkout(NewWorkout entity);

    void deleteNewWorkout(NewWorkout newWorkout);

    void deleteAllNewWorkout();
}
