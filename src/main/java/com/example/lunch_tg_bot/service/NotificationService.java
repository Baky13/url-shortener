package com.example.lunch_tg_bot.service;

import com.example.lunch_tg_bot.config.BotConfig;
import com.example.lunch_tg_bot.repository.UserRepository;
import com.example.lunch_tg_bot.repository.UserResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final TelegramBotService telegramBotService;
    private final LunchListService lunchListService;
    private final UserResponseRepository userResponseRepository;
    private final BotConfig botConfig;
    private final UserRepository userRepository;
    
    private final Set<Long> usersNeedingReminders = ConcurrentHashMap.newKeySet();

    public NotificationService(@Lazy TelegramBotService telegramBotService,
                             LunchListService lunchListService,
                             UserResponseRepository userResponseRepository,
                             BotConfig botConfig,
                             UserRepository userRepository) {
        this.telegramBotService = telegramBotService;
        this.lunchListService = lunchListService;
        this.userResponseRepository = userResponseRepository;
        this.botConfig = botConfig;
        this.userRepository = userRepository;
    }

    // Проверка на рабочий день (Пн-Пт)
    private boolean isWorkday() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    // Проверка на рабочее время для напоминаний (10:00 - 14:00)
    private boolean isReminderTime() {
        LocalTime now = LocalTime.now();
        return now.isAfter(LocalTime.of(10, 0)) && now.isBefore(LocalTime.of(14, 0));
    }

    // Очистка списка напоминаний в начале дня
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void resetDailyReminders() {
        usersNeedingReminders.clear();
        log.info("Ежедневный список напоминаний очищен");
    }

    // Утренняя рассылка вопросов об обеде (только в рабочие дни)
    @Scheduled(cron = "0 0 10 * * MON-FRI")
    public void sendMorningLunchQuestions() {
        if (!isWorkday()) {
            return;
        }

        log.info("Начало утренней рассылки вопросов об обеде");
        
        // Отправляем общий вопрос в чат
        telegramBotService.sendMessageToChat("🍽️ Время решать, будете ли вы сегодня обедать!");
        telegramBotService.sendLunchQuestion(botConfig.getChatId());

        // Отправляем персональные вопросы активным пользователям
        List<Long> activeUsers = telegramBotService.getAllActiveUsers();
        
        for (Long userId : activeUsers) {
            if (!lunchListService.hasUserRespondedToday(userId)) {
                telegramBotService.sendLunchQuestionToUser(userId);
                usersNeedingReminders.add(userId);
                log.debug("Добавлен пользователь {} в список напоминаний", userId);
            }
        }
    }

    // Периодические напоминания (каждые 30 минут в рабочее время)
    @Scheduled(fixedRateString = "#{@botConfig.reminderIntervalMinutes * 60 * 1000}")
    public void sendReminders() {
        if (!isWorkday() || !isReminderTime()) {
            return;
        }

        List<Long> usersWhoResponded = userResponseRepository.findUserIdsByResponseDate(LocalDate.now());
        List<Long> allUsers = telegramBotService.getAllActiveUsers();
        List<Long> usersWithoutResponse = allUsers.stream()
                .filter(userId -> !usersWhoResponded.contains(userId))
                .filter(userId -> !usersNeedingReminders.contains(userId))
                .toList();

        if (!usersWithoutResponse.isEmpty()) {
            telegramBotService.sendMessageToChat(
                "⏰ Напоминание для тех, кто еще не ответил на вопрос об обеде! " +
                "Еще " + usersWithoutResponse.size() + " человек(а) не ответили(а)."
            );
            telegramBotService.sendLunchQuestion(botConfig.getChatId());
            
            usersNeedingReminders.addAll(usersWithoutResponse);
            log.info("Отправлены напоминания для {} пользователей", usersWithoutResponse.size());
        }
    }

    // Финальный список обедающих (11:30 в рабочие дни)
    @Scheduled(cron = "0 30 11 * * MON-FRI")
    public void sendFinalLunchList() {
        if (!isWorkday()) {
            return;
        }

        String finalList = lunchListService.getFullTodayList();
        telegramBotService.sendMessageToChat("📋 Окончательный список на обед:\n\n" + finalList);
        
        // Очищаем список напоминаний после финального списка
        usersNeedingReminders.clear();
        log.info("Отправлен финальный список обедающих");
    }

    public void sendLunchQuestionsToAllUsers() {
        List<Long> activeUsers = telegramBotService.getAllActiveUsers();
        
        for (Long userId : activeUsers) {
            if (!lunchListService.hasUserRespondedToday(userId)) {
                telegramBotService.sendLunchQuestionToUser(userId);
                usersNeedingReminders.add(userId);
            }
        }
    }

    // Управление пользователями в списке напоминаний
    public void removeUserFromReminders(Long userId) {
        usersNeedingReminders.remove(userId);
        log.debug("Пользователь {} удален из списка напоминаний", userId);
    }

    public void addUserToReminders(Long userId) {
        usersNeedingReminders.add(userId);
        log.debug("Пользователь {} добавлен в список напоминаний", userId);
    }

    public List<Long> getUsersForReminders() {
        return List.copyOf(usersNeedingReminders);
    }

    public void clearAllReminders() {
        usersNeedingReminders.clear();
        log.info("Список напоминаний очищен");
    }
}
