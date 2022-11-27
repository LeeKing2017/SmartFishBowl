package com.gachon.fishbowl.service;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserSet;
import com.gachon.fishbowl.repository.UserSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSetService {

    private final UserSetRepository userSetRepository;

    /**
     *
     * @param deviceId
     * @return UserSetRepository에서 DeviceId 객체로 검색한, 유저가 설정한 정보 cf. 온도, 수위, 탁도, ph, 먹이 시간
     */
    Optional<UserSet> getUserSet(DeviceId deviceId) {
        return userSetRepository.findByDeviceId(deviceId);
    }
}
