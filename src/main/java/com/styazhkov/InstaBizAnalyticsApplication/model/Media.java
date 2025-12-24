package com.styazhkov.InstaBizAnalyticsApplication.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;

@Entity
@Table(name = "media")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class Media {

    @Id
    private String igMediaId;

    private String mediaType; // PHOTO, VIDEO, REELS, STORY

    private String permalink;

    private String caption;

    private java.time.LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private InstagramAccount account;

    // Связь с метриками (один-к-одному или отдельная таблица)
    @OneToOne(mappedBy = "media", cascade = CascadeType.ALL)
    private MediaInsights insights;

    public String getIgMediaId() {
        return igMediaId;
    }

    public void setIgMediaId(String igMediaId) {
        this.igMediaId = igMediaId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public InstagramAccount getAccount() {
        return account;
    }

    public void setAccount(InstagramAccount account) {
        this.account = account;
    }

    public MediaInsights getInsights() {
        return insights;
    }

    public void setInsights(MediaInsights insights) {
        this.insights = insights;
    }
}
