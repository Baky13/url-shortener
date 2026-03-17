package com.example.lunch_tg_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class BotConfig {
    
    private String botToken;
    private String botUsername;
    private String chatId;
    private int reminderIntervalMinutes = 30;
    private String lunchTime = "12:00";
    private String finalListTime = "11:30";
    
    public String getBotToken() {
        return botToken;
    }
    
    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
    
    public String getBotUsername() {
        return botUsername;
    }
    
    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }
    
    public String getChatId() {
        return chatId;
    }
    
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
    public int getReminderIntervalMinutes() {
        return reminderIntervalMinutes;
    }
    
    public void setReminderIntervalMinutes(int reminderIntervalMinutes) {
        this.reminderIntervalMinutes = reminderIntervalMinutes;
    }
    
    public String getLunchTime() {
        return lunchTime;
    }
    
    public void setLunchTime(String lunchTime) {
        this.lunchTime = lunchTime;
    }
    
    public String getFinalListTime() {
        return finalListTime;
    }
    
    public void setFinalListTime(String finalListTime) {
        this.finalListTime = finalListTime;
    }
}
