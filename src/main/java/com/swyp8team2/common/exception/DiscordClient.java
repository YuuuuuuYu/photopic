package com.swyp8team2.common.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DiscordClient {

    private final RestClient restClient;
    private final String discordWebhookUrl;

    public DiscordClient(
            RestClient restClient,
            @Value("${discord.webhook.url}") String discordWebhookUrl) {
        this.restClient = restClient;
        this.discordWebhookUrl = discordWebhookUrl;
    }

    public void sendAlarm(DiscordMessage request) {
        restClient.post()
                .uri(discordWebhookUrl)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
