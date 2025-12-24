package com.styazhkov.InstaBizAnalyticsApplication.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MetaMediaResponse(List<MediaItem> data) {
    public record MediaItem(
            String id,
            String caption,
            String media_type,  // PHOTO, VIDEO, CAROUSEL_ALBUM, REELS
            String permalink,
            LocalDateTime timestamp
    ) {}
}