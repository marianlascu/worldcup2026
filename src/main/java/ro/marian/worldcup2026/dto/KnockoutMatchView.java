package ro.marian.worldcup2026.dto;

import java.time.LocalDateTime;

public class KnockoutMatchView {

    private Long id;
    private Integer matchNo;
    private String stage;

    private String teamA;
    private String teamB;

    private String rawTeamA;
    private String rawTeamB;

    private Integer scoreA;
    private Integer scoreB;

    private LocalDateTime kickoffAt;
    private String venueCity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMatchNo() {
        return matchNo;
    }

    public void setMatchNo(Integer matchNo) {
        this.matchNo = matchNo;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public String getRawTeamA() {
        return rawTeamA;
    }

    public void setRawTeamA(String rawTeamA) {
        this.rawTeamA = rawTeamA;
    }

    public String getRawTeamB() {
        return rawTeamB;
    }

    public void setRawTeamB(String rawTeamB) {
        this.rawTeamB = rawTeamB;
    }

    public Integer getScoreA() {
        return scoreA;
    }

    public void setScoreA(Integer scoreA) {
        this.scoreA = scoreA;
    }

    public Integer getScoreB() {
        return scoreB;
    }

    public void setScoreB(Integer scoreB) {
        this.scoreB = scoreB;
    }

    public LocalDateTime getKickoffAt() {
        return kickoffAt;
    }

    public void setKickoffAt(LocalDateTime kickoffAt) {
        this.kickoffAt = kickoffAt;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }
}