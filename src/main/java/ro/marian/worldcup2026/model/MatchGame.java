package ro.marian.worldcup2026.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_game")
public class MatchGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="match_no", unique = true)
    private Integer matchNo;

    @Column(name="stage")
    private String stage;

    @Column(name="group_name")
    private String groupName;

    @Column(name="team_a")
    private String teamA;

    @Column(name="team_b")
    private String teamB;

    @Column(name="kickoff_at")
    private LocalDateTime kickoffAt;

    @Column(name="venue_city")
    private String venueCity;

    @Column(name="venue_country")
    private String venueCountry;

    @Column(name="score_a")
    private Integer scoreA;

    @Column(name="score_b")
    private Integer scoreB;
    
    @Column(name = "tournament_id")
    private Long tournamentId;    

    public Long getId() { return id; }

    public Integer getMatchNo() { return matchNo; }
    public void setMatchNo(Integer matchNo) { this.matchNo = matchNo; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getTeamA() { return teamA; }
    public void setTeamA(String teamA) { this.teamA = teamA; }

    public String getTeamB() { return teamB; }
    public void setTeamB(String teamB) { this.teamB = teamB; }

    public LocalDateTime getKickoffAt() { return kickoffAt; }
    public void setKickoffAt(LocalDateTime kickoffAt) { this.kickoffAt = kickoffAt; }

    public String getVenueCity() { return venueCity; }
    public void setVenueCity(String venueCity) { this.venueCity = venueCity; }

    public String getVenueCountry() { return venueCountry; }
    public void setVenueCountry(String venueCountry) { this.venueCountry = venueCountry; }

    public Integer getScoreA() { return scoreA; }
    public void setScoreA(Integer scoreA) { this.scoreA = scoreA; }

    public Integer getScoreB() { return scoreB; }
    public void setScoreB(Integer scoreB) { this.scoreB = scoreB; }
    
    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }    
}