package com.gachon.fishbowl.service;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.Sensing;
import com.gachon.fishbowl.repository.SensingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SensingService {
    private final SensingRepository sensingRepository;

    /**
     *
     * @param deviceId
     * @return SensingRepository에서 deviceId로 검색한 Sensing 객체
     */
    public Optional<Sensing> getSensingData(DeviceId deviceId) {
        return sensingRepository.findByDeviceId(deviceId);
    }
}
