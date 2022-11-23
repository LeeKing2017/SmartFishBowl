package com.gachon.fishbowl.sevice;

import com.gachon.fishbowl.entity.Sensing;
import com.gachon.fishbowl.entity.UserSet;
import com.gachon.fishbowl.entity.UserSetFoodTime;
import com.gachon.fishbowl.repository.UserSetFoodTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSetFoodTimeService {

    private final UserSetFoodTimeRepository userSetFoodTimeRepository;

    /**
     *
     * @param userSet
     * @return UserSetFoodTimeRepository에서 Sensing 객체를 통해 검색한, 사용자가 설정한 먹이 수급 시간 리스트
     */
    Optional<List<UserSetFoodTime>> getUserSetFoodTime(UserSet userSet) {
        return userSetFoodTimeRepository.findAllByUserSet(userSet);
    }
}
