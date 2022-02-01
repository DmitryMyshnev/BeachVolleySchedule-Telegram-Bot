package com.enterprise.myshnev.telegrambot.scheduler.repository.user;

import com.enterprise.myshnev.telegrambot.scheduler.model.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic,Long> {
}
