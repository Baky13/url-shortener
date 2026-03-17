package com.example.lunch_tg_bot.dto;

public class UserStats {
    private Long totalResponses;
    private Long yesCount;
    private Long noCount;

    public UserStats(Long totalResponses, Long yesCount, Long noCount) {
        this.totalResponses = totalResponses;
        this.yesCount = yesCount;
        this.noCount = noCount;
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
