package com.example.lunch_tg_bot.service;

import com.example.lunch_tg_bot.config.BotConfig;
import com.example.lunch_tg_bot.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class NotificationService {

    private final TelegramBotService telegramBotService;
    private final LunchListService lunchListService;
    private final ReminderService reminderService;
    private final BotConfig botConfig;
    private final UserRepository userRepository;

    public NotificationService(TelegramBotService telegramBotService,
                              LunchListService lunchListService,
                              ReminderService reminderService,
                              BotConfig botConfig,
                              UserRepository userRepository) {
        this.telegramBotService = telegramBotService;
        this.lunchListService = lunchListService;
        this.reminderService = reminderService;
        this.botConfig = botConfig;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void sendMorningLunchQuestions() {
        List<Long> activeUsers = telegramBotService.getAllActiveUsers();
        
        for (Long userId : activeUsers) {
            if (!lunchListService.hasUserRespondedToday(userId)) {
                telegramBotService.sendLunchQuestionToUser(userId);
                reminderService.addUserToReminders(userId);
            }
        }
    }

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void sendReminders() {
        List<Long> usersNeedingReminders = reminderService.getUsersForReminders();
        
        for (Long userId : usersNeedingReminders) {
            if (!lunchListService.hasUserRespondedToday(userId)) {
                telegramBotService.sendLunchQuestionToUser(userId);
            }
        }
    }

    @Scheduled(cron = "0 30 11 * * *")
    public void sendFinalLunchList() {
        String fullList = lunchListService.getFullTodayList();
        telegramBotService.sendMessageToChat("📋 Окончательный список на обед:\n\n" + fullList);
        reminderService.clearAllReminders();
    }

    public void sendLunchQuestionsToAllUsers() {
        List<Long> activeUsers = telegramBotService.getAllActiveUsers();
        
        for (Long userId : activeUsers) {
            if (!lunchListService.hasUserRespondedToday(userId)) {
                telegramBotService.sendLunchQuestionToUser(userId);
                reminderService.addUserToReminders(userId);
            }
        }
    }
}
