package com.example.lunch_tg_bot.repository;

import com.example.lunch_tg_bot.entity.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
    
    // Найти ответ пользователя по конкретной дате
    Optional<UserResponse> findByUserIdAndResponseDate(Long userId, LocalDate responseDate);
    
    // Найти все ответы пользователя
    List<UserResponse> findByUserIdOrderByResponseDateDesc(Long userId);
    
    // Найти все ответы за конкретную дату
    List<UserResponse> findByResponseDateOrderByResponseTime(LocalDate responseDate);
    
    // Подсчитать ответы за дату
    long countByResponseDate(LocalDate responseDate);
    
    // Подсчитать ответы "Да" за дату
    long countByResponseDateAndAnswer(LocalDate responseDate, UserResponse.Answer answer);
    
    // Проверить отвечал ли пользователь сегодня
    boolean existsByUserIdAndResponseDate(Long userId, LocalDate responseDate);
    
    // Найти ответы "Да" за конкретную дату
    List<UserResponse> findByResponseDateAndAnswer(LocalDate responseDate, UserResponse.Answer answer);
    
    // Получить ID пользователей, которые ответили за дату
    @Query("SELECT DISTINCT ur.userId FROM UserResponse ur WHERE ur.responseDate = :responseDate")
    List<Long> findUserIdsByResponseDate(@Param("responseDate") LocalDate responseDate);
    
    // Удалить ответ пользователя за конкретную дату
    void deleteByUserIdAndResponseDate(Long userId, LocalDate responseDate);
}
