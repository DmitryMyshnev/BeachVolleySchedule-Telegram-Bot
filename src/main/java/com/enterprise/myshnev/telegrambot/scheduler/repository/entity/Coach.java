package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class Coach {
    private String chatId;
    private String firstName;
    private String lastName;

    public Coach(String chatId,String firstName, String lastName) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return  "'" + chatId +
                "', '" + firstName +
                "', '" + lastName +
                "'";
    }
}
