package com.gachon.fishbowl.repository;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSetRepository extends JpaRepository<UserSet, Long> {
    Optional<UserSet> findByDeviceId(DeviceId deviceId);
}
