package ro.marian.worldcup2026.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "prediction",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_prediction_league_user_match",
                        columnNames = {"league_id", "user_id", "match_id"}
                )
        }
)
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="league_id", nullable = false)
    private Long leagueId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="match_id", nullable = false)
    private Long matchId;

    @Column(name="predicted_score_a")
    private Integer predictedScoreA;

    @Column(name="predicted_score_b")
    private Integer predictedScoreB;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Integer getPredictedScoreA() {
        return predictedScoreA;
    }

    public void setPredictedScoreA(Integer predictedScoreA) {
        this.predictedScoreA = predictedScoreA;
    }

    public Integer getPredictedScoreB() {
        return predictedScoreB;
    }

    public void setPredictedScoreB(Integer predictedScoreB) {
        this.predictedScoreB = predictedScoreB;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}