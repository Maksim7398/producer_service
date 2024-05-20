package com.producer.producer_service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfiguration {

    @Bean
    public WebClient defaultWebClient(@Value("${app.security.url}") String url) {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
