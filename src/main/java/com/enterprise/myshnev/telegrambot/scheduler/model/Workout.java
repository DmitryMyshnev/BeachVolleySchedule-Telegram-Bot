package com.enterprise.myshnev.telegrambot.scheduler.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity

public class Workout {
    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coach_id")
    private String coachId;
    @Column(name = "day_of_week")
    private String dayOfWeek;
    private String time;
    @Column(name = "max_count_user")
    private Integer maxCountUser;
    @Column(name = "is_active")
    private boolean isActive;

    public Workout() {
    }

    public Workout(String coachId, String dayOfWeek, String time) {
        this.coachId = coachId;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        isActive = false;
    }

}
