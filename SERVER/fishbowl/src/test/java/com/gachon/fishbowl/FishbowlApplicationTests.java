package com.gachon.fishbowl;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserDevice;
import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.entity.role.Role;
import com.gachon.fishbowl.repository.DeviceIdRepository;
import com.gachon.fishbowl.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@SpringBootTest
class FishbowlApplicationTests {
	@Autowired
	private UserDeviceRepository userDeviceRepository;
	@Autowired
	private DeviceIdRepository deviceIdRepository;

//	@Test
//	void contextLoads() {
//		Long deviceId = 123L;
//		String email = "jw1010110@naver.com";
//		Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
//		log.info(byId.toString());
//		log.info((userDeviceRepository.findByDeviceId(byId.get()).toString()));
//		log.info(":asdf{}",userDeviceRepository.findByDeviceId(byId.get()).get().getUserId().toString());
//		Boolean equals = userDeviceRepository.findByDeviceId(byId.get()).get().getUserId().getId().equals(email);
//		log.info(equals.toString());
//
//	}

}
