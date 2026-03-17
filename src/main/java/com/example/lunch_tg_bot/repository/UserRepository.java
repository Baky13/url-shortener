package com.example.lunch_tg_bot.repository;

import com.example.lunch_tg_bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Найти пользователя по Telegram ID
    Optional<User> findByTelegramUserId(Long telegramUserId);
    
    // Найти всех активных пользователей
    List<User> findByIsActiveTrue();
    
    // Проверить существует ли пользователь с таким Telegram ID
    boolean existsByTelegramUserId(Long telegramUserId);
    
    // Найти пользователей по имени
    List<User> findByDisplayNameContainingIgnoreCase(String displayName);
    
    // Найти пользователей по списку Telegram ID
    List<User> findByTelegramUserIdIn(List<Long> telegramUserIds);
}
