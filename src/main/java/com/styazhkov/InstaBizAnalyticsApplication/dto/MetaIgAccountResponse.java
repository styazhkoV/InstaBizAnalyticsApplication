package com.styazhkov.InstaBizAnalyticsApplication.dto;

public record MetaIgAccountResponse(IgAccount instagram_business_account) {
    public record IgAccount(String id) {}
}