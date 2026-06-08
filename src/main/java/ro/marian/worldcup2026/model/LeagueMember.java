package ro.marian.worldcup2026.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "league_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_league_member_user",
                        columnNames = {"league_id", "user_id"}
                )
        }
)
public class LeagueMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="league_id", nullable = false)
    private Long leagueId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="role", nullable = false, length = 20)
    private String role = "PLAYER";

    @Column(name="joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name="active_yn", nullable = false, length = 1)
    private String activeYn = "Y";

    @PrePersist
    public void prePersist() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
        if (role == null || role.isBlank()) {
            role = "PLAYER";
        }
        if (activeYn == null || activeYn.isBlank()) {
            activeYn = "Y";
        }
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public String getActiveYn() {
        return activeYn;
    }

    public void setActiveYn(String activeYn) {
        this.activeYn = activeYn;
    }
}