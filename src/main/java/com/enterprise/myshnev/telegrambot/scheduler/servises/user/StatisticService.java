package com.enterprise.myshnev.telegrambot.scheduler.servises.user;

import com.enterprise.myshnev.telegrambot.scheduler.model.Statistic;

import java.util.List;

public interface StatisticService {
    void saveStatistic(Statistic stat);

    List<Statistic> findAllStatistic();
}
