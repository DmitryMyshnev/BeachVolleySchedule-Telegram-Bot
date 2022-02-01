package com.enterprise.myshnev.telegrambot.scheduler.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private String chatId;
    @Column(name = "user_name")
    private String userName;
    private String workout;
    private String action;
    private String date;

    public Statistic(String chatId, String userName, String workout, String action, String date) {
        this.chatId = chatId;
        this.userName = userName;
        this.workout = workout;
        this.action = action;
        this.date = date;
    }

    public Statistic() {
    }


}
