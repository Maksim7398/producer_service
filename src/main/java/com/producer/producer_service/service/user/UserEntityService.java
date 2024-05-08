package com.producer.producer_service.service.user;

import com.producer.producer_service.controller.request.ProfileRequest;
import com.producer.producer_service.persist.entity.UserEntity;
import com.producer.producer_service.persist.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;

    @Transactional
    public UUID findIdByEmail(String email) {
        final UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        UserEntity newUser = userEntityRepository.save(userEntity);
        return newUser.getId();
    }

    public UserEntity updateUser(UUID id,ProfileRequest profileRequest){
        final UserEntity userEntity = userEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userEntity.setFirstName(profileRequest.getFirstName());
        userEntity.setLastName(profileRequest.getLastName());
        userEntity.setPhone(profileRequest.getPhone());
        return userEntityRepository.save(userEntity);
    }

}
