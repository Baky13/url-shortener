package com.example.lunch_tg_bot.dto;

import java.time.LocalDate;

public class DailyStats {
    private LocalDate date;
    private Long totalResponses;
    private Long yesCount;
    private Long noCount;

    public DailyStats(LocalDate date, Long totalResponses, Long yesCount, Long noCount) {
        this.date = date;
        this.totalResponses = totalResponses;
        this.yesCount = yesCount;
        this.noCount = noCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTotalResponses() {
        return totalResponses;
    }

    public void setTotalResponses(Long totalResponses) {
        this.totalResponses = totalResponses;
    }

    public Long getYesCount() {
        return yesCount;
    }

    public void setYesCount(Long yesCount) {
        this.yesCount = yesCount;
    }

    public Long getNoCount() {
        return noCount;
    }

    public void setNoCount(Long noCount) {
        this.noCount = noCount;
    }
}
