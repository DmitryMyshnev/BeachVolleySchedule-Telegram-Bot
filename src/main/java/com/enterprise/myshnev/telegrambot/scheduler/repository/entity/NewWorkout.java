package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class NewWorkout {
    private String userId;
    private String firstName;
    private String lastName;
    private boolean reserve;

    @Override
    public String toString() {
        return
                "'" + userId + "'" +
                ", '" + firstName + "'" +
                ", '" + lastName + "'" +
                ", " + reserve ;
    }
}
