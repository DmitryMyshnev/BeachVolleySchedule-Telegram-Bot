package com.enterprise.myshnev.telegrambot.scheduler.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "New_workout")
public class NewWorkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private String chatId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private boolean reserve;
    @OneToOne(fetch = FetchType.EAGER)
    private Workout workout;
    @OneToOne(fetch = FetchType.EAGER)
    private SentMessages sentMessages;
}
