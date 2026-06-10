package ro.marian.worldcup2026.dto;

import java.time.LocalDateTime;

public class LivePanelMatchRow {

    private final Long matchId;
    private final Integer matchNo;
    private final String stage;
    private final String teamA;
    private final String teamB;
    private final LocalDateTime kickoffAt;
    private final Integer scoreA;
    private final Integer scoreB;
    private final boolean live;
    private final boolean finished;

    public LivePanelMatchRow(Long matchId,
                             Integer matchNo,
                             String stage,
                             String teamA,
                             String teamB,
                             LocalDateTime kickoffAt,
                             Integer scoreA,
                             Integer scoreB,
                             boolean live,
                             boolean finished) {
        this.matchId = matchId;
        this.matchNo = matchNo;
        this.stage = stage;
        this.teamA = teamA;
        this.teamB = teamB;
        this.kickoffAt = kickoffAt;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.live = live;
        this.finished = finished;
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

    public String getTeamA() {
        return teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public LocalDateTime getKickoffAt() {
        return kickoffAt;
    }

    public Integer getScoreA() {
        return scoreA;
    }

    public Integer getScoreB() {
        return scoreB;
    }

    public boolean isLive() {
        return live;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getScoreText() {
        if (scoreA == null || scoreB == null) {
            return "-";
        }
        return scoreA + "-" + scoreB;
    }
}