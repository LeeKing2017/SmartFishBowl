package com.gachon.fishbowl.service;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserDevice;
import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;

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

    public Boolean isMatchedEmailWithDeviceId(String email, Long deviceId) {
        DeviceId build = DeviceId.builder().id(deviceId).build();
        if (userDeviceRepository.findByDeviceId(build).get().getUserId().equals(email)) {
            return true;
        }
        return false;
    }
}
