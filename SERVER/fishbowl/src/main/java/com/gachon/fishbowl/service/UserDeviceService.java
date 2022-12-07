package com.gachon.fishbowl.service;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserDevice;
import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.repository.DeviceIdRepository;
import com.gachon.fishbowl.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final DeviceIdRepository deviceIdRepository;
    /**
     *
     * @param userId
     * @return UserDeviceRepository에서 UserId 객체로 검색한 UserDevice 객체
     */
    public Optional<UserDevice> getUserDeviceByUserId(UserId userId) {
        return userDeviceRepository.findByUserId(userId);
    }

    /**
     *
     * @param deviceId
     * @return UserDeviceRepository에서 deviceId 객체로 검색한 UserDevice 객체
     */
    public Optional<UserDevice> getUserDeviceByDeviceId(DeviceId deviceId) {
        return userDeviceRepository.findByDeviceId(deviceId);
    }

    public Optional<List<UserDevice>> getAllUserDeviceByUserId(UserId userId) {
        return userDeviceRepository.findAllByUserId(userId);
    }

    /**
     *
     * @param email
     * @param deviceId
     * @return 매개변수로 전달된 email과 deviceId가 일치하는지 확인 후 true or false 리턴
     */
    public Boolean isMatchedEmailWithDeviceId(String email, Long deviceId) {
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
        if (userDeviceRepository.findByDeviceId(byId.get()).get().getUserId().getId().equals(email)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param email
     * @param deviceId
     * @return 매개변수로 전달된 email과 deviceId가 매칭되는 UserDevice 객체가 있는지 확인 후 true Or false 리턴
     */
    public Boolean isPresentMatchedEmailWithDeviceId(String email, Long deviceId) {
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<List<UserDevice>> allByDeviceId = userDeviceRepository.findAllByDeviceId(build);

        if (allByDeviceId.isPresent()) {
            Boolean b = allByDeviceId.get().stream().anyMatch(a -> a.getUserId().getId().equals(email) && a.getDeviceId().getId().equals(deviceId));
            return b;
        }
        return false;
    }



    public UserDevice saveUserDevice(UserDevice userDevice) {
        return userDeviceRepository.save(userDevice);
    }

    public void deleteUserDevice(UserDevice userDevice) {
        userDeviceRepository.delete(userDevice);
    }
}
