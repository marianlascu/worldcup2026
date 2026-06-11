package ro.marian.worldcup2026.dto;

import java.time.LocalDateTime;

public class FootballDataMatchDto {

    private Long externalId;
    private String status;

    private String homeTeamName;
    private String awayTeamName;

    private LocalDateTime utcKickoffAt;

    private Integer homeScore;
    private Integer awayScore;

    public FootballDataMatchDto() {
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public LocalDateTime getUtcKickoffAt() {
        return utcKickoffAt;
    }

    public void setUtcKickoffAt(LocalDateTime utcKickoffAt) {
        this.utcKickoffAt = utcKickoffAt;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }
}