package com.enterprise.myshnev.telegrambot.scheduler.model;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "User")
public class TelegramUser {
    @Id
    private String id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Role role;
    private boolean active;

    public TelegramUser(String id, String firstName, String lastName, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = true;
        this.role = role;
    }

    public TelegramUser() {
    }

    public boolean isEqualsRole(String rolName) {
        return this.role.getName().equals(rolName);
    }
}
