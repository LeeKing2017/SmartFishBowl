package com.gachon.fishbowl.sevice;

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


}
