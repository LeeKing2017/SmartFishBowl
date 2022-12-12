package com.gachon.fishbowl.service;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.entity.UserSet;
import com.gachon.fishbowl.entity.UserSetFoodTime;
import com.gachon.fishbowl.repository.DeviceIdRepository;
import com.gachon.fishbowl.repository.UserSetFoodTimeRepository;
import com.gachon.fishbowl.repository.UserSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSetFoodTimeService {

    private final UserSetFoodTimeRepository userSetFoodTimeRepository;
    private final UserSetRepository userSetRepository;
    private final DeviceIdRepository deviceIdRepository;
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
    public UserSetFoodTime setFirstFoodTimeAndCnt(Long deviceId, LocalTime time, Integer cnt) {
        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(byId.get());

        if (userSetFoodTimeRepository.findByUserSet(byDeviceId.get()).isEmpty()) { //저장된게 없음 생성 후 저장
            UserSetFoodTime userSetFoodTime = UserSetFoodTime.builder()
                    .firstTime(time)
                    .numberOfFirstFeedings(cnt)
                    .userSet(byDeviceId.get())
                    .build();
            UserSetFoodTime save = userSetFoodTimeRepository.save(userSetFoodTime);
            return save;
        }else {
            Optional<UserSetFoodTime> byUserSet = userSetFoodTimeRepository.findByUserSet(byDeviceId.get());
            byUserSet.get().setFirstTime(time);
            byUserSet.get().setNumberOfFirstFeedings(cnt);
            UserSetFoodTime save = userSetFoodTimeRepository.save(byUserSet.get());
            return save;
        }
    }

    public UserSetFoodTime setSecondFoodTimeAndCnt(Long deviceId, LocalTime time, Integer cnt) {
        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(byId.get());

        if (userSetFoodTimeRepository.findByUserSet(byDeviceId.get()).isEmpty()) { //저장된게 없음 생성 후 저장
            UserSetFoodTime userSetFoodTime = UserSetFoodTime.builder()
                    .secondTime(time)
                    .numberOfSecondFeedings(cnt)
                    .userSet(byDeviceId.get())
                    .build();
            UserSetFoodTime save = userSetFoodTimeRepository.save(userSetFoodTime);
            return save;
        }else {
            Optional<UserSetFoodTime> byUserSet = userSetFoodTimeRepository.findByUserSet(byDeviceId.get());
            byUserSet.get().setSecondTime(time);
            byUserSet.get().setNumberOfSecondFeedings(cnt);
            UserSetFoodTime save = userSetFoodTimeRepository.save(byUserSet.get());
            return save;
        }
    }

    public UserSetFoodTime setThirdFoodTimeAndCnt(Long deviceId, LocalTime time, Integer cnt) {
        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(byId.get());

        if (userSetFoodTimeRepository.findByUserSet(byDeviceId.get()).isEmpty()) { //저장된게 없음 생성 후 저장
            UserSetFoodTime userSetFoodTime = UserSetFoodTime.builder()
                    .thirdTime(time)
                    .numberOfThirdFeedings(cnt)
                    .userSet(byDeviceId.get())
                    .build();
            UserSetFoodTime save = userSetFoodTimeRepository.save(userSetFoodTime);
            return save;
        }else {
            Optional<UserSetFoodTime> byUserSet = userSetFoodTimeRepository.findByUserSet(byDeviceId.get());
            byUserSet.get().setThirdTime(time);
            byUserSet.get().setNumberOfThirdFeedings(cnt);
            UserSetFoodTime save = userSetFoodTimeRepository.save(byUserSet.get());
            return save;
        }
    }

    public void deleteFoodTimeAndCnt(Long deviceId) {
        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(byId.get());
        if (byDeviceId.isPresent()) {
            Optional<List<UserSetFoodTime>> allByUserSet = userSetFoodTimeRepository.findAllByUserSet(byDeviceId.get());
            allByUserSet.get().iterator().forEachRemaining(e->userSetFoodTimeRepository.delete(e));
        }else {
            log.info("userSetFoodTime 없어서 삭제 안함");
        }

    }
}
