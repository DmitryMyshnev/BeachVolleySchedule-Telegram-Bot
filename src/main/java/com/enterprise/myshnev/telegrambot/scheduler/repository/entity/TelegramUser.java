package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;


@Data
public class TelegramUser {

    private String chatId;
    private String firstName;
    private String lastName;
    private boolean active;
    private boolean coach;

    public TelegramUser(String chatId, String firstName, String lastName) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = true;
        this.coach = false;
    }

    public TelegramUser() {
        active = false;
        coach = false;
    }

    @Override
    public String toString() {
        return "'" + chatId +
                "', '" + firstName +
                "', '" + lastName +
                "', " + active +
                ", " + coach;
    }
    public String fullName(){
        return  "'" + chatId +
                "', '" + firstName +
                "', '" + lastName +
                "'";
    }
}
