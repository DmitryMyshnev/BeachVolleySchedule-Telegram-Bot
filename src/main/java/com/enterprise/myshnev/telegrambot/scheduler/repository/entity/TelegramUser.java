package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;


@Data
//@Entity
//@Table(name = "Users")
public class TelegramUser {

    // @Id
    //  @Column(name = "chat_id")
    private String chatId;
    //  @Column(name = "first_name")
    private String firstName;
    //  @Column(name = "last_name")
    private String lastName;
    //   @Column(name = "admin")
    private boolean admin;
    //  @Column(name = "coach")
    private boolean coach;

    public TelegramUser(String chatId, String firstName, String lastName) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.admin = false;
        this.coach = false;
    }

    public TelegramUser() {
        admin = false;
        coach = false;
    }

    @Override
    public String toString() {
        return "'" + chatId +
                "', '" + firstName +
                "', '" + lastName +
                "', " + admin +
                ", " + coach;
    }
    public String fullName(){
        return  "'" + chatId +
                "', '" + firstName +
                "', '" + lastName +
                "'";
    }
}
