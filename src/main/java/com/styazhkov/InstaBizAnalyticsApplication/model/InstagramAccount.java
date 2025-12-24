package com.styazhkov.InstaBizAnalyticsApplication.model;


import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "instagram_accounts")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class InstagramAccount {
    public void setIgUserId(String igUserId) {
        this.igUserId = igUserId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public void setLastSyncedAt(LocalDateTime lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIgUserId() {
        return igUserId;
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public LocalDateTime getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public LocalDateTime getLastSyncedAt() {
        return lastSyncedAt;
    }

    public User getUser() {
        return user;
    }

    @Id
    private String igUserId; // ID аккаунта из Instagram Graph API (строка)

    private String username;

    private String accessToken; // long-lived token

    private LocalDateTime tokenExpiresAt;

    private LocalDateTime lastSyncedAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Можно добавить: profilePic, followersCount и т.д.
}
