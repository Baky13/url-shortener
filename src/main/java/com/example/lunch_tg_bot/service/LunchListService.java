package com.example.lunch_tg_bot.service;

import com.example.lunch_tg_bot.entity.User;
import com.example.lunch_tg_bot.entity.UserResponse;
import com.example.lunch_tg_bot.repository.UserRepository;
import com.example.lunch_tg_bot.repository.UserResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LunchListService {

    private final UserResponseRepository userResponseRepository;
    private final UserRepository userRepository;

    public LunchListService(UserResponseRepository userResponseRepository, UserRepository userRepository) {
        this.userResponseRepository = userResponseRepository;
        this.userRepository = userRepository;
    }

    public String getTodayLunchList() {
        List<UserResponse> todayResponses = userResponseRepository.findByResponseDateAndAnswer(
            LocalDate.now(), UserResponse.Answer.YES
        );

        if (todayResponses.isEmpty()) {
            return "😔 Сегодня никто не обедает";
        }

        String lunchList = todayResponses.stream()
                .map(this::formatUserResponse)
                .collect(Collectors.joining("\n"));

        return "🍽️ Будут обедать (" + todayResponses.size() + "):\n\n" + lunchList;
    }

    public String getTodayNotLunchList() {
        List<UserResponse> todayResponses = userResponseRepository.findByResponseDateAndAnswer(
            LocalDate.now(), UserResponse.Answer.NO
        );

        if (todayResponses.isEmpty()) {
            return "";
        }

        String notLunchList = todayResponses.stream()
                .map(this::formatUserResponse)
                .collect(Collectors.joining("\n"));

        return "\n\n😔 Не будут обедать (" + todayResponses.size() + "):\n\n" + notLunchList;
    }

    public String getFullTodayList() {
        String lunchList = getTodayLunchList();
        String notLunchList = getTodayNotLunchList();
        
        return lunchList + notLunchList;
    }

    public String getLunchListForDate(LocalDate date) {
        List<UserResponse> responses = userResponseRepository.findByResponseDateOrderByResponseTime(date);

        if (responses.isEmpty()) {
            return "😔 На " + date + " никто не ответил";
        }

        List<UserResponse> yesResponses = responses.stream()
                .filter(r -> r.getAnswer() == UserResponse.Answer.YES)
                .toList();

        List<UserResponse> noResponses = responses.stream()
                .filter(r -> r.getAnswer() == UserResponse.Answer.NO)
                .toList();

        StringBuilder result = new StringBuilder();
        result.append("📋 Список на ").append(date).append(":\n\n");

        if (!yesResponses.isEmpty()) {
            result.append("🍽️ Будут обедать (").append(yesResponses.size()).append("):\n");
            yesResponses.stream()
                    .map(this::formatUserResponse)
                    .forEach(user -> result.append(user).append("\n"));
        }

        if (!noResponses.isEmpty()) {
            result.append("\n😔 Не будут обедать (").append(noResponses.size()).append("):\n");
            noResponses.stream()
                    .map(this::formatUserResponse)
                    .forEach(user -> result.append(user).append("\n"));
        }

        return result.toString();
    }

    private String formatUserResponse(UserResponse response) {
        Optional<User> user = userRepository.findByTelegramUserId(response.getUserId());
        String name;
        
        if (user.isPresent() && user.get().getDisplayName() != null) {
            name = user.get().getDisplayName();
        } else {
            name = response.getFirstName();
            if (response.getLastName() != null && !response.getLastName().trim().isEmpty()) {
                name += " " + response.getLastName();
            }
        }
        
        if (response.getUsername() != null && !response.getUsername().trim().isEmpty()) {
            name += " (@" + response.getUsername() + ")";
        }

        return "• " + name;
    }

    public int getTodayLunchCount() {
        return (int) userResponseRepository.countByResponseDateAndAnswer(
            LocalDate.now(), UserResponse.Answer.YES
        );
    }

    public int getTodayNotLunchCount() {
        return (int) userResponseRepository.countByResponseDateAndAnswer(
            LocalDate.now(), UserResponse.Answer.NO
        );
    }

    public boolean hasUserRespondedToday(Long userId) {
        return userResponseRepository.existsByUserIdAndResponseDate(userId, LocalDate.now());
    }

    @Transactional
    public void removeTodayResponse(Long userId) {
        userResponseRepository.deleteByUserIdAndResponseDate(userId, LocalDate.now());
    }
}
