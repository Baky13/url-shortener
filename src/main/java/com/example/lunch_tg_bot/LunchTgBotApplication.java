package com.example.lunch_tg_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LunchTgBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(LunchTgBotApplication.class, args);
    }

}
