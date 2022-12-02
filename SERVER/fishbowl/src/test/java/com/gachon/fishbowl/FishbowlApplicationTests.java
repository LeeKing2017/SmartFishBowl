package com.gachon.fishbowl;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserDevice;
import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.entity.role.Role;
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

//	@Test
//	void contextLoads() {
//		String email = "jw1010110@naver.com";
//		Long deviceId = 123L;
//
//		DeviceId build = DeviceId.builder().id(deviceId).build();
//
//		Optional<List<UserDevice>> allByDeviceId = userDeviceRepository.findAllByDeviceId(build);
//
//		if (allByDeviceId.isPresent()) {
//			Boolean b = allByDeviceId.get().stream().anyMatch(a -> a.getUserId().getId().equals(email) && a.getDeviceId().getId().equals(deviceId));
//			log.info(b.toString());
//		} else {
//			log.info("값 없음");
//		}
//
//	}

}
