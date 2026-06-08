package ro.marian.worldcup2026.dto;

import java.time.LocalDateTime;

public class LeagueMemberRow {

    private Long userId;
    private String username;
    private String fullName;
    private String role;
    private LocalDateTime joinedAt;

    public LeagueMemberRow(Long userId,
                           String username,
                           String fullName,
                           String role,
                           LocalDateTime joinedAt) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}