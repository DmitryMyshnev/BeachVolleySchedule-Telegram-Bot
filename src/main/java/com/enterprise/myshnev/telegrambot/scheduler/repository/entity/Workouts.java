package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class Workouts {
    private Integer id;
    private String coachId;
    private String dayOfWeek;
    private String time;
    private Integer maxCountUser;
    private boolean isActive;

    public Workouts() {
    }

    public Workouts(String coachId, String dayOfWeek, String time) {
        this.coachId = coachId;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        isActive = false;
    }

    @Override
    public String toString() {
        return
                "'" + coachId + "'" +
                ", '" + dayOfWeek + "'" +
                ", '" + time+ "'" +
                ", " + isActive;
    }
}
