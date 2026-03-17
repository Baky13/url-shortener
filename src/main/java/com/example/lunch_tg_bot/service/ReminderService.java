package com.example.lunch_tg_bot.service;

import com.example.lunch_tg_bot.config.BotConfig;
import com.example.lunch_tg_bot.repository.UserResponseRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReminderService {

    private final TelegramBotService telegramBotService;
    private final UserResponseRepository userResponseRepository;
    private final BotConfig botConfig;
    private final Set<Long> usersNeedingReminders = new HashSet<>();

    public ReminderService(@Lazy TelegramBotService telegramBotService,
                         UserResponseRepository userResponseRepository,
                         BotConfig botConfig) {
        this.telegramBotService = telegramBotService;
        this.userResponseRepository = userResponseRepository;
        this.botConfig = botConfig;
    }

    public void setLunchListService(LunchListService lunchListService) {
        telegramBotService.lunchListService = lunchListService;
    }

    @Scheduled(cron = "0 0 10 * * MON-FRI")
    public void sendInitialLunchQuestion() {
        usersNeedingReminders.clear();
        telegramBotService.sendMessageToChat("🍽️ Время решать, будете ли вы сегодня обедать!");
        telegramBotService.sendLunchQuestion(botConfig.getChatId());
    }

    @Scheduled(fixedRateString = "#{@botConfig.reminderIntervalMinutes * 60 * 1000}")
    public void sendReminders() {
        if (!isWorkday() || !isReminderTime()) {
            return;
        }

        List<Long> usersWhoResponded = userResponseRepository.findUserIdsByResponseDate(LocalDate.now());
        
        if (usersWhoResponded.isEmpty()) {
            telegramBotService.sendMessageToChat("⏰ Напоминание: никто еще не ответил на вопрос об обеде!");
            telegramBotService.sendLunchQuestion(botConfig.getChatId());
            return;
        }

        List<Long> allUsers = getAllActiveUsers();
        List<Long> usersWithoutResponse = allUsers.stream()
                .filter(userId -> !usersWhoResponded.contains(userId))
                .filter(userId -> !usersNeedingReminders.contains(userId))
                .toList();

        if (!usersWithoutResponse.isEmpty()) {
            telegramBotService.sendMessageToChat(
                "⏰ Напоминание для тех, кто еще не ответил на вопрос об обеде! " +
                "Еще " + (usersWithoutResponse.size()) + " человек(а) не ответили(а)."
            );
            telegramBotService.sendLunchQuestion(botConfig.getChatId());
            
            usersNeedingReminders.addAll(usersWithoutResponse);
        }
    }

    @Scheduled(cron = "0 30 11 * * MON-FRI")
    public void sendFinalReminder() {
        if (!isWorkday()) return;

        String lunchList = telegramBotService.lunchListService.getTodayLunchList();
        telegramBotService.sendMessageToChat(
            "⏰ Последний шанс ответить! Через 30 минут финальный список обедающих.\n\n" +
            "Текущий список:\n" + lunchList
        );
    }

    @Scheduled(cron = "0 0 12 * * MON-FRI")
    public void sendFinalLunchList() {
        if (!isWorkday()) return;

        String finalList = telegramBotService.lunchListService.getTodayLunchList();
        telegramBotService.sendMessageToChat(
            "📋 Финальный список обедающих на сегодня:\n\n" + finalList + "\n\n" +
            "Приятного аппетита! 😊"
        );
    }

    private boolean isWorkday() {
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        return dayOfWeek >= 1 && dayOfWeek <= 5;
    }

    private boolean isReminderTime() {
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 45);
        
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    private List<Long> getAllActiveUsers() {
        return telegramBotService.getAllActiveUsers();
    }

    public void removeUserFromReminders(Long userId) {
        usersNeedingReminders.remove(userId);
    }

    public void resetReminders() {
        usersNeedingReminders.clear();
    }

    public void addUserToReminders(Long userId) {
        usersNeedingReminders.add(userId);
    }

    public List<Long> getUsersForReminders() {
        return List.copyOf(usersNeedingReminders);
    }

    public void clearAllReminders() {
        usersNeedingReminders.clear();
    }
}
