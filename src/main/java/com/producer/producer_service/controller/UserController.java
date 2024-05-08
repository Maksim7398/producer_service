package com.producer.producer_service.controller;

import com.producer.producer_service.controller.request.LoginRequest;
import com.producer.producer_service.controller.request.ProfileRequest;
import com.producer.producer_service.exception.NotAuthorizedException;
import com.producer.producer_service.persist.entity.UserEntity;
import com.producer.producer_service.security.TokenProvider;
import com.producer.producer_service.service.user.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserEntityService userEntityService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signin")
    public String signin(@RequestBody LoginRequest loginRequest) {
        return tokenProvider.getToken(true,loginRequest).block();
    }

    @PostMapping
    public UUID findIdByEmail(@RequestBody String email) {
        return userEntityService.findIdByEmail(email);
    }

    @PatchMapping(path = "/auth/{id}")
    public UserEntity update(@PathVariable(name = "id") UUID id,
                             @RequestBody ProfileRequest profileRequest) {
        try {
            return userEntityService.updateUser(id, profileRequest);
        }catch (NotAuthorizedException ex){
            throw ex;
        }
    }
}
