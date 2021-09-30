package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

import lombok.Data;

@Data
public class CoachVsUserJoin {
    private String coachId;
    private String UserId;

    public CoachVsUserJoin(String coachId, String userId) {
        this.coachId = coachId;
        UserId = userId;
    }

    @Override
    public String toString() {
        return    coachId + ", " + UserId;
    }
}
