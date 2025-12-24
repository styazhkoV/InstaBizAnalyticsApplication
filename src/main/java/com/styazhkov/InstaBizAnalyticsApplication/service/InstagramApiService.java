package com.styazhkov.InstaBizAnalyticsApplication.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.styazhkov.InstaBizAnalyticsApplication.dto.*;
import com.styazhkov.InstaBizAnalyticsApplication.model.InstagramAccount;
import com.styazhkov.InstaBizAnalyticsApplication.model.Media;
import com.styazhkov.InstaBizAnalyticsApplication.model.MediaInsights;
import com.styazhkov.InstaBizAnalyticsApplication.repository.InstagramAccountRepository;
import com.styazhkov.InstaBizAnalyticsApplication.repository.MediaInsightsRepository;
import com.styazhkov.InstaBizAnalyticsApplication.repository.MediaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class InstagramApiService {

    private final InstagramAccountRepository accountRepository;
    private final MediaRepository mediaRepository;
    private final MediaInsightsRepository insightsRepository;

    @Value("${meta.client-id}")
    private String clientId;

    @Value("${meta.client-secret}")
    private String clientSecret;

    private final RestClient restClient = RestClient.create();

    // Явный конструктор — Spring сам внедрит зависимости
    public InstagramApiService(InstagramAccountRepository accountRepository,
                               MediaRepository mediaRepository,
                               MediaInsightsRepository insightsRepository) {
        this.accountRepository = accountRepository;
        this.mediaRepository = mediaRepository;
        this.insightsRepository = insightsRepository;
    }

    public String exchangeToLongLivedToken(String shortLivedToken) {
        String url = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/v20.0/oauth/access_token")
                .queryParam("grant_type", "fb_exchange_token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("fb_exchange_token", shortLivedToken)
                .toUriString();

        MetaTokenResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .body(MetaTokenResponse.class);

        return response != null ? response.access_token() : null;
    }

    public String getInstagramAccountId(String longLivedToken) {
        String url = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/v20.0/me/accounts")
                .queryParam("access_token", longLivedToken)
                .toUriString();

        MetaPagesResponse pages = restClient.get()
                .uri(url)
                .retrieve()
                .body(MetaPagesResponse.class);

        if (pages != null && pages.data() != null && !pages.data().isEmpty()) {
            String pageToken = pages.data().getFirst().access_token();
            String igUrl = "https://graph.facebook.com/v20.0/" + pages.data().getFirst().id() +
                    "?fields=instagram_business_account&access_token=" + pageToken;

            MetaIgAccountResponse igResponse = restClient.get()
                    .uri(igUrl)
                    .retrieve()
                    .body(MetaIgAccountResponse.class);

            return igResponse != null && igResponse.instagram_business_account() != null
                    ? igResponse.instagram_business_account().id()
                    : null;
        }
        return null;
    }

    public void syncAccountData(String igUserId) {
        Optional<InstagramAccount> accountOpt = accountRepository.findById(igUserId);
        if (accountOpt.isEmpty() || accountOpt.get().getAccessToken() == null) {
            throw new IllegalStateException("Account not configured or token missing");
        }

        InstagramAccount account = accountOpt.get();
        String token = account.getAccessToken();

        String mediaUrl = "https://graph.instagram.com/v20.0/" + igUserId + "/media" +
                "?fields=id,caption,media_type,permalink,timestamp&access_token=" + token;

        MetaMediaResponse mediaResponse = restClient.get()
                .uri(mediaUrl)
                .retrieve()
                .body(MetaMediaResponse.class);

        if (mediaResponse != null && mediaResponse.data() != null) {
            for (MetaMediaResponse.MediaItem item : mediaResponse.data()) {
                Media media = mediaRepository.findById(item.id())
                        .orElse(new Media());
                media.setIgMediaId(item.id());
                media.setCaption(item.caption());
                media.setMediaType(item.media_type());
                media.setPermalink(item.permalink());
                media.setTimestamp(item.timestamp());
                media.setAccount(account);
                mediaRepository.save(media);

                fetchAndSaveInsights(item.id(), token, media);
            }
        }

        account.setLastSyncedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private void fetchAndSaveInsights(String mediaId, String token, Media media) {
        String insightsUrl = "https://graph.instagram.com/v20.0/" + mediaId +
                "/insights?metric=impressions,reach,engagement,likes,comments,saved&access_token=" + token;

        MetaInsightsResponse response = restClient.get()
                .uri(insightsUrl)
                .retrieve()
                .body(MetaInsightsResponse.class);

        if (response != null && response.data() != null) {
            MediaInsights insights = insightsRepository.findByMedia(media)
                    .orElse(new MediaInsights());

            for (MetaInsightsResponse.InsightItem item : response.data()) {
                switch (item.name()) {
                    case "impressions" -> insights.setImpressions(item.values().getFirst().value());
                    case "reach" -> insights.setReach(item.values().getFirst().value());
                    case "engagement" -> insights.setEngagement(item.values().getFirst().value());
                    case "likes" -> insights.setLikes(item.values().getFirst().value());
                    case "comments" -> insights.setComments(item.values().getFirst().value());
                    case "saved" -> insights.setSaved(item.values().getFirst().value());
                }
            }
            insights.setFetchedAt(LocalDateTime.now());
            insights.setMedia(media);
            insightsRepository.save(insights);
        }
    }
}