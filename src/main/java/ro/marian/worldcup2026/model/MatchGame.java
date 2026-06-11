package ro.marian.worldcup2026.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_game")
public class MatchGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_no", unique = true)
    private Integer matchNo;

    @Column(name = "stage")
    private String stage;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "team_a")
    private String teamA;

    @Column(name = "team_b")
    private String teamB;

    @Column(name = "kickoff_at")
    private LocalDateTime kickoffAt;

    @Column(name = "venue_city")
    private String venueCity;

    @Column(name = "venue_country")
    private String venueCountry;

    /* =========================================================
       OFFICIAL VALIDATED SCORE
       Used by Tables / Predictions / Rankings
       ========================================================= */

    @Column(name = "score_a")
    private Integer scoreA;

    @Column(name = "score_b")
    private Integer scoreB;

    /* =========================================================
       TOURNAMENT
       ========================================================= */

    @Column(name = "tournament_id")
    private Long tournamentId;

    /* =========================================================
       API SCORE
       Imported from external provider
       ========================================================= */

    @Column(name = "api_score_a")
    private Integer apiScoreA;

    @Column(name = "api_score_b")
    private Integer apiScoreB;

    @Column(name = "api_status")
    private String apiStatus;

    @Column(name = "api_updated_at")
    private LocalDateTime apiUpdatedAt;

    /* =========================================================
       MANUAL SCORE
       Entered by administrator
       ========================================================= */

    @Column(name = "manual_score_a")
    private Integer manualScoreA;

    @Column(name = "manual_score_b")
    private Integer manualScoreB;

    @Column(name = "manual_updated_at")
    private LocalDateTime manualUpdatedAt;

    @Column(name = "manual_updated_by")
    private String manualUpdatedBy;

    /* =========================================================
       VALIDATION INFO
       ========================================================= */

    @Column(name = "score_source")
    private String scoreSource;

    @Column(name = "score_validated_yn")
    private String scoreValidatedYn;

    @Column(name = "score_validated_at")
    private LocalDateTime scoreValidatedAt;

    @Column(name = "score_validated_by")
    private String scoreValidatedBy;
    
    @Column(name = "api_match_id")
    private Long apiMatchId;    

    /* =========================================================
       GETTERS / SETTERS
       ========================================================= */

    public Long getId() {
        return id;
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

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    public String getVenueCountry() {
        return venueCountry;
    }

    public void setVenueCountry(String venueCountry) {
        this.venueCountry = venueCountry;
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

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getApiScoreA() {
        return apiScoreA;
    }

    public void setApiScoreA(Integer apiScoreA) {
        this.apiScoreA = apiScoreA;
    }

    public Integer getApiScoreB() {
        return apiScoreB;
    }

    public void setApiScoreB(Integer apiScoreB) {
        this.apiScoreB = apiScoreB;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public LocalDateTime getApiUpdatedAt() {
        return apiUpdatedAt;
    }

    public void setApiUpdatedAt(LocalDateTime apiUpdatedAt) {
        this.apiUpdatedAt = apiUpdatedAt;
    }

    public Integer getManualScoreA() {
        return manualScoreA;
    }

    public void setManualScoreA(Integer manualScoreA) {
        this.manualScoreA = manualScoreA;
    }

    public Integer getManualScoreB() {
        return manualScoreB;
    }

    public void setManualScoreB(Integer manualScoreB) {
        this.manualScoreB = manualScoreB;
    }

    public LocalDateTime getManualUpdatedAt() {
        return manualUpdatedAt;
    }

    public void setManualUpdatedAt(LocalDateTime manualUpdatedAt) {
        this.manualUpdatedAt = manualUpdatedAt;
    }

    public String getManualUpdatedBy() {
        return manualUpdatedBy;
    }

    public void setManualUpdatedBy(String manualUpdatedBy) {
        this.manualUpdatedBy = manualUpdatedBy;
    }

    public String getScoreSource() {
        return scoreSource;
    }

    public void setScoreSource(String scoreSource) {
        this.scoreSource = scoreSource;
    }

    public String getScoreValidatedYn() {
        return scoreValidatedYn;
    }

    public void setScoreValidatedYn(String scoreValidatedYn) {
        this.scoreValidatedYn = scoreValidatedYn;
    }

    public LocalDateTime getScoreValidatedAt() {
        return scoreValidatedAt;
    }

    public void setScoreValidatedAt(LocalDateTime scoreValidatedAt) {
        this.scoreValidatedAt = scoreValidatedAt;
    }

    public String getScoreValidatedBy() {
        return scoreValidatedBy;
    }

    public void setScoreValidatedBy(String scoreValidatedBy) {
        this.scoreValidatedBy = scoreValidatedBy;
    }
    
    public Long getApiMatchId() {
    return apiMatchId;
}

    public void setApiMatchId(Long apiMatchId) {
        this.apiMatchId = apiMatchId;
    }
}