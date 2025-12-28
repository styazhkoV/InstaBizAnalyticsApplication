package com.styazhkov.InstaBizAnalyticsApplication.service;

import com.styazhkov.InstaBizAnalyticsApplication.dto.*;
import com.styazhkov.InstaBizAnalyticsApplication.model.*;
import com.styazhkov.InstaBizAnalyticsApplication.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j // Добавляем логирование
@Service
@Transactional
public class InstagramApiService {

    private static final String BASE_FB_GRAPH_URL = "https://graph.facebook.com/v20.0";
    private static final String BASE_IG_GRAPH_URL = "https://graph.instagram.com/v20.0";

    private final InstagramAccountRepository accountRepository;
    private final MediaRepository mediaRepository;
    private final MediaInsightsRepository insightsRepository;
    private final RestClient restClient;

    @Value("${meta.client-id}")
    private String clientId;

    @Value("${meta.client-secret}")
    private String clientSecret;

    public InstagramApiService(InstagramAccountRepository accountRepository,
                               MediaRepository mediaRepository,
                               MediaInsightsRepository insightsRepository) {
        this.accountRepository = accountRepository;
        this.mediaRepository = mediaRepository;
        this.insightsRepository = insightsRepository;
        this.restClient = RestClient.create();
    }

    /**
     * Обмен короткого токена на долгосрочный (60 дней)
     */
    public String exchangeToLongLivedToken(String shortLivedToken) {
        log.info("Exchanging short-lived token for long-lived");
        String url = UriComponentsBuilder.fromHttpUrl(BASE_FB_GRAPH_URL + "/oauth/access_token")
                .queryParam("grant_type", "fb_exchange_token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("fb_exchange_token", shortLivedToken)
                .toUriString();

        MetaTokenResponse response = restClient.get().uri(url).retrieve().body(MetaTokenResponse.class);
        return response != null ? response.access_token() : null;
    }

    /**
     * Автоматическое получение ID бизнес-аккаунта Instagram через Facebook Pages
     */
    public String getInstagramAccountId(String longLivedToken) {
        log.info("Fetching Instagram Business Account ID");
        String url = UriComponentsBuilder.fromHttpUrl(BASE_FB_GRAPH_URL + "/me/accounts")
                .queryParam("access_token", longLivedToken)
                .toUriString();

        MetaPagesResponse pages = restClient.get().uri(url).retrieve().body(MetaPagesResponse.class);

        if (pages == null || pages.data() == null || pages.data().isEmpty()) {
            log.warn("No Facebook pages found for this user");
            return null;
        }

        // Берем первую страницу и ищем связанный Instagram Business Account
        String pageId = pages.data().getFirst().id();
        String pageToken = pages.data().getFirst().access_token();

        String igUrl = UriComponentsBuilder.fromHttpUrl(BASE_FB_GRAPH_URL + "/" + pageId)
                .queryParam("fields", "instagram_business_account")
                .queryParam("access_token", pageToken)
                .toUriString();

        MetaIgAccountResponse igResponse = restClient.get().uri(igUrl).retrieve().body(MetaIgAccountResponse.class);

        return (igResponse != null && igResponse.instagram_business_account() != null)
                ? igResponse.instagram_business_account().id()
                : null;
    }

    /**
     * Синхронизация данных: посты и их метрики
     */
    public void syncAccountData(String igUserId) {
        InstagramAccount account = accountRepository.findById(igUserId)
                .orElseThrow(() -> new IllegalStateException("Account " + igUserId + " not found in DB"));

        String token = account.getAccessToken();
        log.info("Starting sync for account: {}", igUserId);

        String mediaUrl = UriComponentsBuilder.fromHttpUrl(BASE_IG_GRAPH_URL + "/" + igUserId + "/media")
                .queryParam("fields", "id,caption,media_type,permalink,timestamp")
                .queryParam("access_token", token)
                .toUriString();

        MetaMediaResponse mediaResponse = restClient.get().uri(mediaUrl).retrieve().body(MetaMediaResponse.class);

        if (mediaResponse != null && mediaResponse.data() != null) {
            mediaResponse.data().forEach(item -> {
                Media media = mediaRepository.findById(item.id()).orElse(new Media());
                updateMediaInfo(media, item, account);
                fetchAndSaveInsights(item.id(), token, media);
            });
        }

        account.setLastSyncedAt(LocalDateTime.now());
        accountRepository.save(account);
        log.info("Sync completed for account: {}", igUserId);
    }

    private void updateMediaInfo(Media media, MetaMediaResponse.MediaItem item, InstagramAccount account) {
        media.setIgMediaId(item.id());
        media.setCaption(item.caption());
        media.setMediaType(item.media_type());
        media.setPermalink(item.permalink());
        media.setTimestamp(item.timestamp());
        media.setAccount(account);
        mediaRepository.save(media);
    }

    private void fetchAndSaveInsights(String mediaId, String token, Media media) {
        String insightsUrl = UriComponentsBuilder.fromHttpUrl(BASE_IG_GRAPH_URL + "/" + mediaId + "/insights")
                .queryParam("metric", "impressions,reach,engagement,likes,comments,saved")
                .queryParam("access_token", token)
                .toUriString();

        try {
            MetaInsightsResponse response = restClient.get().uri(insightsUrl).retrieve().body(MetaInsightsResponse.class);

            if (response != null && response.data() != null) {
                MediaInsights insights = insightsRepository.findByMedia(media).orElse(new MediaInsights());

                response.data().forEach(item -> {
                    int value = item.values().isEmpty() ? 0 : item.values().getFirst().value();
                    switch (item.name()) {
                        case "impressions" -> insights.setImpressions(value);
                        case "reach" -> insights.setReach(value);
                        case "engagement" -> insights.setEngagement(value);
                        case "likes" -> insights.setLikes(value);
                        case "comments" -> insights.setComments(value);
                        case "saved" -> insights.setSaved(value);
                    }
                });

                insights.setFetchedAt(LocalDateTime.now());
                insights.setMedia(media);
                insightsRepository.save(insights);
            }
        } catch (Exception e) {
            log.error("Failed to fetch insights for media {}: {}", mediaId, e.getMessage());
        }
    }
}