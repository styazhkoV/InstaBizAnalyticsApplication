package com.styazhkov.InstaBizAnalyticsApplication.dto;

public record MetaTokenResponse(String access_token, String token_type, int expires_in) {}