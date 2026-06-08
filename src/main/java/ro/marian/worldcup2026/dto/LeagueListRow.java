package ro.marian.worldcup2026.dto;

import java.time.LocalDateTime;

public class LeagueListRow {

    private Long leagueId;
    private String leagueName;
    private String leagueCode;
    private String tournamentName;
    private String memberRole;
    private LocalDateTime joinedAt;

    public LeagueListRow(Long leagueId,
                         String leagueName,
                         String leagueCode,
                         String tournamentName,
                         String memberRole,
                         LocalDateTime joinedAt) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.leagueCode = leagueCode;
        this.tournamentName = tournamentName;
        this.memberRole = memberRole;
        this.joinedAt = joinedAt;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public String getLeagueCode() {
        return leagueCode;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}