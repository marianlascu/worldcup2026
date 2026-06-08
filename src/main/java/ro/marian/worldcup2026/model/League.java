package ro.marian.worldcup2026.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "league")
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="code", nullable = false, unique = true, length = 40)
    private String code;

    @Column(name="name", nullable = false, length = 120)
    private String name;

    @Column(name="tournament_id", nullable = false)
    private Long tournamentId;

    @Column(name="owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="active_yn", nullable = false, length = 1)
    private String activeYn = "Y";

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (activeYn == null || activeYn.isBlank()) {
            activeYn = "Y";
        }
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getActiveYn() {
        return activeYn;
    }

    public void setActiveYn(String activeYn) {
        this.activeYn = activeYn;
    }
}