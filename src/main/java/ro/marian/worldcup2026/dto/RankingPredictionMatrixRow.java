package ro.marian.worldcup2026.dto;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class RankingPredictionMatrixRow {

    private Long matchId;
    private Integer matchNo;
    private String stage;
    private String groupName;

    private String teamA;
    private String teamB;

    private LocalDateTime kickoffAt;

    private Integer officialScoreA;
    private Integer officialScoreB;
    private String officialScore;

    private boolean started;

    private Map<String, PredictionMatrixCell> playerPredictions = new LinkedHashMap<>();

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public LocalDateTime getKickoffAt() {
        return kickoffAt;
    }

    public void setKickoffAt(LocalDateTime kickoffAt) {
        this.kickoffAt = kickoffAt;
    }

    public Integer getOfficialScoreA() {
        return officialScoreA;
    }

    public void setOfficialScoreA(Integer officialScoreA) {
        this.officialScoreA = officialScoreA;
    }

    public Integer getOfficialScoreB() {
        return officialScoreB;
    }

    public void setOfficialScoreB(Integer officialScoreB) {
        this.officialScoreB = officialScoreB;
    }

    public String getOfficialScore() {
        return officialScore;
    }

    public void setOfficialScore(String officialScore) {
        this.officialScore = officialScore;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public Map<String, PredictionMatrixCell> getPlayerPredictions() {
        return playerPredictions;
    }

    public void setPlayerPredictions(Map<String, PredictionMatrixCell> playerPredictions) {
        this.playerPredictions = playerPredictions;
    }
}