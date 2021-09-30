package com.enterprise.myshnev.telegrambot.scheduler.db;

public enum DbStatusResponse {
    OK("ok"),
    FAIL("fail"),
    EXIST("does exist");
    private final String statusResponse;
    DbStatusResponse(String statusResponse) {
        this.statusResponse = statusResponse;
    }

    public String getStatus() {
        return statusResponse;
    }
}
