package com.enterprise.myshnev.telegrambot.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class ApplicationBotScheduler {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationBotScheduler.class, args);
	}
}
