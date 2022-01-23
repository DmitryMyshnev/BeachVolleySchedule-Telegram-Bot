package com.enterprise.myshnev.telegrambot.scheduler.servises.workout;

import com.enterprise.myshnev.telegrambot.scheduler.model.NewWorkout;
import com.enterprise.myshnev.telegrambot.scheduler.model.Workout;
import com.enterprise.myshnev.telegrambot.scheduler.repository.workout.NewWorkoutRepository;
import com.enterprise.myshnev.telegrambot.scheduler.repository.workout.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final NewWorkoutRepository newWorkoutRepository;

    @Autowired
    public WorkoutServiceImpl(WorkoutRepository workoutRepository, NewWorkoutRepository newWorkoutRepository) {
        this.workoutRepository = workoutRepository;
        this.newWorkoutRepository = newWorkoutRepository;
    }

    public void save(Workout workout) {
        workoutRepository.save(workout);
    }

    @Override
    @Cacheable("workouts")
    public List<Workout> findAllWorkout() {
        return workoutRepository.findAll();
    }

    @Override
    public void delete(NewWorkout user) {
        newWorkoutRepository.delete(user);
    }

    @Override
    public List<NewWorkout> findAllJoinedUsers(Long id) {
        return newWorkoutRepository.findByWorkoutId(id);
    }

    @Override
    public void updateNewWorkout(NewWorkout newWorkout) {
        newWorkoutRepository.save(newWorkout);
    }

    @Override
    public void saveNewWorkout(NewWorkout entity) {
        newWorkoutRepository.save(entity);
    }

    @Override
    @CacheEvict(cacheNames = "workouts", allEntries = true)
    public void saveWorkout(Workout workout) {
        workoutRepository.save(workout);
    }

    @Override
    public void deleteNewWorkout(NewWorkout newWorkout) {
        newWorkoutRepository.delete(newWorkout);
    }

    @CacheEvict(cacheNames = "workouts", allEntries = true)
    @Override
    public void updateWorkout(Workout workout) {
        workoutRepository.save(workout);
    }

    @CacheEvict(cacheNames = "workouts", allEntries = true)
    @Override
    public void deleteWorkout(Workout workout) {
        workoutRepository.delete(workout);
    }

    @Override
    public Workout findWorkoutByTime(String weekOfDay, String timeWorkout) {
        return workoutRepository.findByDayOfWeekAndTime(weekOfDay, timeWorkout);
    }

    @Override
    public Optional<NewWorkout> findJoinedUser(String chatId, Long workoutId) {
        return newWorkoutRepository.findByChatIdAndWorkoutId(chatId, workoutId);
    }

    @Override
    public Optional<Workout> findWorkout(Long id) {
        return workoutRepository.findById(id);
    }

}
