package com.example.lunch_tg_bot.config;

import com.example.lunch_tg_bot.service.LunchListService;
import com.example.lunch_tg_bot.service.ReminderService;
import com.example.lunch_tg_bot.service.TelegramBotService;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import jakarta.annotation.PostConstruct;

@Configuration
public class ServiceConfiguration {

    private final TelegramBotService telegramBotService;
    private final ReminderService reminderService;
    private final LunchListService lunchListService;

    public ServiceConfiguration(TelegramBotService telegramBotService,
                               ReminderService reminderService,
                               LunchListService lunchListService) {
        this.telegramBotService = telegramBotService;
        this.reminderService = reminderService;
        this.lunchListService = lunchListService;
    }

    @PostConstruct
    public void configureServices() {
        telegramBotService.lunchListService = lunchListService;
        reminderService.setLunchListService(lunchListService);
        
        // Инициализация бота
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBotService);
            System.out.println("Бот успешно зарегистрирован в Telegram");
        } catch (TelegramApiException e) {
            System.err.println("Ошибка регистрации бота: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
