package com.gachon.fishbowl.service;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.entity.UserSet;
import com.gachon.fishbowl.entity.UserSetFoodTime;
import com.gachon.fishbowl.repository.UserSetFoodTimeRepository;
import com.gachon.fishbowl.repository.UserSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSetFoodTimeService {

    private final UserSetFoodTimeRepository userSetFoodTimeRepository;
    private final UserSetRepository userSetRepository;
    private final UserIdService userIdService;

    /**
     *
     * @param userSet
     * @return UserSetFoodTimeRepository에서 Sensing 객체를 통해 검색한, 사용자가 설정한 먹이 수급 시간 리스트
     */
    Optional<List<UserSetFoodTime>> getUserSetFoodTime(UserSet userSet) {
        return userSetFoodTimeRepository.findAllByUserSet(userSet);
    }

    /**
     *
     * @param deviceId
     * @param time
     * @param cnt
     * @return 매개변수로 전달받은 deviceid에 해당하는 먹이시간과 먹이 지급 횟수 저장 후 UserSetFoodTime 리턴
     */
    public UserSetFoodTime setFoodTimeAndCnt(Long deviceId, LocalTime time, Integer cnt) {
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        UserSetFoodTime userSetFoodTime = UserSetFoodTime.builder()
                .foodTime(time)
                .feedCnt(cnt)
                .userSet(byDeviceId.get())
                .build();
        UserSetFoodTime save = userSetFoodTimeRepository.save(userSetFoodTime);
        return save;
    }
}
