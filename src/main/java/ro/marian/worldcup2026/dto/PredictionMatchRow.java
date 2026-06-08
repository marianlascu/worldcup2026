package ro.marian.worldcup2026.dto;

import java.time.LocalDateTime;

public class PredictionMatchRow {

    private Long matchId;
    private Integer matchNo;
    private String stage;
    private String groupName;
    private String teamA;
    private String teamB;
    private LocalDateTime kickoffAt;
    private String venueCity;

    private Integer officialScoreA;
    private Integer officialScoreB;

    private Integer predictedScoreA;
    private Integer predictedScoreB;

    public PredictionMatchRow(Long matchId,
                              Integer matchNo,
                              String stage,
                              String groupName,
                              String teamA,
                              String teamB,
                              LocalDateTime kickoffAt,
                              String venueCity,
                              Integer officialScoreA,
                              Integer officialScoreB,
                              Integer predictedScoreA,
                              Integer predictedScoreB) {
        this.matchId = matchId;
        this.matchNo = matchNo;
        this.stage = stage;
        this.groupName = groupName;
        this.teamA = teamA;
        this.teamB = teamB;
        this.kickoffAt = kickoffAt;
        this.venueCity = venueCity;
        this.officialScoreA = officialScoreA;
        this.officialScoreB = officialScoreB;
        this.predictedScoreA = predictedScoreA;
        this.predictedScoreB = predictedScoreB;
    }

    public Long getMatchId() {
        return matchId;
    }

    public Integer getMatchNo() {
        return matchNo;
    }

    public String getStage() {
        return stage;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTeamA() {
        return teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public LocalDateTime getKickoffAt() {
        return kickoffAt;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public Integer getOfficialScoreA() {
        return officialScoreA;
    }

    public Integer getOfficialScoreB() {
        return officialScoreB;
    }

    public Integer getPredictedScoreA() {
        return predictedScoreA;
    }

    public Integer getPredictedScoreB() {
        return predictedScoreB;
    }
}