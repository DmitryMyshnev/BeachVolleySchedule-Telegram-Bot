package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class NewWorkout {
    private String userId;
    private String firstName;
    private String lastName;
    private String timeWorkout;
    private boolean reserve;
}
