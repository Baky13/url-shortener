package com.example.lunch_tg_bot.config;

import com.example.lunch_tg_bot.service.TelegramBotService;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

@Configuration
public class ServiceConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ServiceConfiguration.class);

    private final TelegramBotService telegramBotService;

    public ServiceConfiguration(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostConstruct
    public void configureServices() {
        // Инициализация бота
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBotService);
            log.info("Бот успешно зарегистрирован в Telegram");
        } catch (TelegramApiException e) {
            log.error("Ошибка регистрации бота: {}", e.getMessage(), e);
        }
    }
}
