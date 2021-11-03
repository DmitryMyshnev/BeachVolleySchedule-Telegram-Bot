package com.enterprise.myshnev.telegrambot.scheduler.repository.entity;

public enum UserAction {
        ADD("add"),
        REMOVE("remove"),
        ADD_TO_RESERVE("add to reserve");
        private final String userAction;

        UserAction(String userAction) {
            this.userAction = userAction;
        }

        public String getUserAction() {
            return userAction;
        }
    }

