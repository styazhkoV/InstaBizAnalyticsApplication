package com.styazhkov.InstaBizAnalyticsApplication.dto;

import java.util.List;

public record MetaInsightsResponse(List<InsightItem> data) {
    public record InsightItem(
            String name,  // impressions, reach, engagement и т.д.
            String description,
            List<Value> values
    ) {}

    public record Value(int value, String end_time) {}
}