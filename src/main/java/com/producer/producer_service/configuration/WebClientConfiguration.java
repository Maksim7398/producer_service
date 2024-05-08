package com.producer.producer_service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfiguration {

    @Bean
    public WebClient defaultWebClient() {
        return WebClient.create("http://localhost:8080");
    }
}
