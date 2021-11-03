package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class Statistic {
    private Integer id;
    private String userId;
    private String userName;
    private String workout;
    private String action;
    private String date;

    public Statistic(String userId, String userName, String workout, String action, String date) {
        this.userId = userId;
        this.userName = userName;
        this.workout = workout;
        this.action = action;
        this.date = date;
    }

    public Statistic() {
    }

    @Override
    public String toString() {
        return
                "'" + userId + "'" +
                ", '" + userName + "'" +
                ", '" + workout + "'" +
                ", '" + action + "'" +
                ", '" + date + "'";
    }

}
