package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
@lombok.Data
public class Data {
    private String chatId;
    private Integer messageId;
    private  String message;
    private InlineKeyboardMarkup keyBoard;
    private String dayOfWeek;
    private String timeWorkout;
    private Integer maxUser;
    private boolean isCouch;

    public Data(String chatId, String message, InlineKeyboardMarkup keyBoard, String timeWorkout, String dayOfWeek, Integer maxUser, boolean isCouch) {
        this.chatId = chatId;
        this.message = message;
        this.keyBoard = keyBoard;
        this.timeWorkout = timeWorkout;
        this.dayOfWeek = dayOfWeek;
        this.isCouch = isCouch;
        this.maxUser = maxUser;
    }
    public Data(String chatId, String message,String timeWorkout) {
        this.chatId = chatId;
        this.message = message;
        this.timeWorkout = timeWorkout;
    }
    public Data(){
        isCouch = false;
    }
}
