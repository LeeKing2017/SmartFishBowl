package com.gachon.fishbowl.service;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.repository.DeviceIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DeviceIdService {
    private final DeviceIdRepository deviceIdRepository;

    /**
     *
     * @param deviceId
     * @return DeviceIdRepository에서 deviceId로 검색한 DeviceId 객체
     */
    public Optional<DeviceId> getDeviceId(Long deviceId) {
        return deviceIdRepository.findById(deviceId);
    }
}
