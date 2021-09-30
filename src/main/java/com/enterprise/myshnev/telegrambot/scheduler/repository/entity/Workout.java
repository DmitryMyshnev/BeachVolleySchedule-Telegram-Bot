package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class Workout {
    private String coachId;
    private String weekOfDay;
    private Integer time;
    private boolean period;
}
