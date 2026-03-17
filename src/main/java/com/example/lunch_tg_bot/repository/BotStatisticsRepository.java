package com.example.lunch_tg_bot.repository;

import com.example.lunch_tg_bot.entity.BotStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BotStatisticsRepository extends JpaRepository<BotStatistics, Long> {
    
    // Найти статистику за дату
    Optional<BotStatistics> findByStatDate(LocalDate statDate);
    
    // Найти статистику за период
    List<BotStatistics> findByStatDateBetweenOrderByStatDate(LocalDate startDate, LocalDate endDate);
    
    // Получить последнюю статистику
    Optional<BotStatistics> findFirstByOrderByStatDateDesc();
    
    // Подсчитать общую статистику за период
    @Query("SELECT " +
           "SUM(b.totalUsers) as totalUsers, " +
           "SUM(b.yesCount) as totalYes, " +
           "SUM(b.noCount) as totalNo " +
           "FROM BotStatistics b " +
           "WHERE b.statDate BETWEEN :startDate AND :endDate")
    Optional<Object[]> getTotalStatsByPeriod(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
}
