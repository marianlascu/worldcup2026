package ro.marian.worldcup2026.dto;

import java.time.LocalDateTime;

public class PredictionMatchRow {

    private final Long matchId;
    private final Integer matchNo;
    private final String stage;
    private final String groupName;
    private final String teamA;
    private final String teamB;
    private final LocalDateTime kickoffAt;
    private final String venueCity;

    private final Integer officialScoreA;
    private final Integer officialScoreB;

    private final Integer predictedScoreA;
    private final Integer predictedScoreB;

    private final boolean locked;

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
                              Integer predictedScoreB,
                              boolean locked) {

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
        this.locked = locked;
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

    public boolean isLocked() {
        return locked;
    }
}