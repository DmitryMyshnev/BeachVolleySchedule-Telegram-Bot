package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class MessageId {
    private Integer id;
    private Integer messageId;
    private String chatId;
    private String time;
    private String dayOfWeek;

    public MessageId(Integer messageId, String chatId, String time, String dayOfWeek) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
    }

    public MessageId() {
    }

    @Override
    public String toString() {
        return messageId + ", '" +
                chatId + "', " +
                "'" + time + "', " +
                "'" + dayOfWeek + "'";
    }
}
