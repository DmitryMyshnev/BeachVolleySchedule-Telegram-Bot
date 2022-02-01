package com.enterprise.myshnev.telegrambot.scheduler.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Sent_messages")
public class SentMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "message_id")
    private Integer messageId;
    @OneToOne(fetch = FetchType.EAGER)
    private TelegramUser user;
    @OneToOne(fetch = FetchType.EAGER)
    private Workout workout;

    public SentMessages(Integer messageId, TelegramUser user, Workout workout) {
        this.messageId = messageId;
        this.user = user;
        this.workout = workout;
    }

    public SentMessages() {
    }

}
