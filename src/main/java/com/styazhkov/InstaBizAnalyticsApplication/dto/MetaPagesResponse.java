package com.styazhkov.InstaBizAnalyticsApplication.dto;

import java.util.List;

public record MetaPagesResponse(List<Page> data) {
    public record Page(String id, String access_token, String name) {}
}