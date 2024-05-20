package com.producer.producer_service.security;

import com.producer.producer_service.controller.request.LoginRequest;
import com.producer.producer_service.controller.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private final WebClient defaultWebClient;

    private Mono<String> cachedToken;

    @SneakyThrows
    public Mono<String> getToken(boolean getFromCache,LoginRequest loginRequest) {
        if (!getFromCache || cachedToken == null) {
            log.info("Retrieve token and cache...");
            cachedToken = retrieveToken(loginRequest)
                    .cache(Duration.ofMinutes(10))
                    .doOnError(throwable -> cachedToken = null)
            ;
        }

        return cachedToken;
    }

    @SneakyThrows
    private Mono<String> retrieveToken(LoginRequest loginRequest) {
        return defaultWebClient.post()
                .uri("/api/v1/auth/signin")
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getToken);
    }
}
