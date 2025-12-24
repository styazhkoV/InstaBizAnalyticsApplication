package com.styazhkov.InstaBizAnalyticsApplication.model;


import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // можно взять из Instagram или отдельный login

    private LocalDateTime createdAt = LocalDateTime.now();

    // Один пользователь — один Instagram аккаунт (пока упростим)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InstagramAccount instagramAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public InstagramAccount getInstagramAccount() {
        return instagramAccount;
    }

    public void setInstagramAccount(InstagramAccount instagramAccount) {
        this.instagramAccount = instagramAccount;
    }
}