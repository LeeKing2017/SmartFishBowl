package com.gachon.fishbowl.service;

import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.repository.UserIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserIdService {

    private final UserIdRepository userIdRepository;

    /**
     *
     * @param email
     * @return UserIdRepository에서 email로 검색한 UserId 객체
     */
    public Optional<UserId> getUserId(String email) {
        return userIdRepository.findById(email);
    }

    /**
     *
     * @param email
     * @return 가입된 유저: true, 가입되지 않은 유저: flase
     */
    public Boolean isRegisteredUsers(String email) {
        if(userIdRepository.findById(email).isEmpty()){ //가입 안된 유저
            return false;
        }
        return true;
    }

    public UserId saveUser(UserId userId) {
        return userIdRepository.save(userId);
    }

    public void deleteUser(UserId userId) {
        userIdRepository.delete(userId);
    }

}
