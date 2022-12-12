package com.gachon.fishbowl;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.UserDevice;
import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.entity.role.Role;
import com.gachon.fishbowl.repository.DeviceIdRepository;
import com.gachon.fishbowl.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Slf4j
@SpringBootTest
class FishbowlApplicationTests {
	@Autowired
	private UserDeviceRepository userDeviceRepository;
	@Autowired
	private DeviceIdRepository deviceIdRepository;

	@Test
	void contextLoads() {
		List<String> strings = new ArrayList<>();
		strings.add("1qjs");
		strings.add("2qjs");
		strings.add("3qjs");
		strings.add("4qjs");

		Iterator<String> iterator = strings.iterator();
		while (iterator.hasNext()) {
			log.info(iterator.next());
		}

	}

}
