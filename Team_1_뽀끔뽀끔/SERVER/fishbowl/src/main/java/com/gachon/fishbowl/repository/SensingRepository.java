package com.gachon.fishbowl.repository;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.Sensing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensingRepository extends JpaRepository<Sensing, Long> {
    Optional<Sensing> findByDeviceId(DeviceId deviceId);
}
