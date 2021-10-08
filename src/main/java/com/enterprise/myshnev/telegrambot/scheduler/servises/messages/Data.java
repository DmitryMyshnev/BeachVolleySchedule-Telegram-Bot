package com.enterprise.myshnev.telegrambot.scheduler.servises.messages;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
@lombok.Data
public class Data {
    private String chatId;
    private Integer messageId;
    private  String message;
    private InlineKeyboardMarkup keyBoard;
    private String weekOfDay;
    private String timeWorkout;
    private boolean isCouch;

    public Data(String chatId, String message, InlineKeyboardMarkup keyBoard, String weekOfDay, String timeWorkout,boolean isCouch) {
        this.chatId = chatId;
        this.message = message;
        this.keyBoard = keyBoard;
        this.weekOfDay = weekOfDay;
        this.timeWorkout = timeWorkout;
        this.isCouch = isCouch;
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
