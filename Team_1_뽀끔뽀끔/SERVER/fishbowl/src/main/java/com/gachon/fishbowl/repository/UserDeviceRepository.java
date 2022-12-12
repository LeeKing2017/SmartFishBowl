package com.gachon.fishbowl.repository;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserDevice;
import com.gachon.fishbowl.entity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<List<UserDevice>> findAllByDeviceId(DeviceId deviceId);

    Optional<List<UserDevice>> findAllByUserId(UserId userId);

    Optional<UserDevice> findByDeviceId(DeviceId deviceId);

    Optional<UserDevice> findByUserId(UserId userId);
}
