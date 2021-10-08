package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class Workouts {
    private Integer id;
    private String coachId;
    private String weekOfDay;
    private String time;
    private Integer maxCountUser;
}
