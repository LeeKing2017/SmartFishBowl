package com.gachon.fishbowl;

import com.gachon.fishbowl.config.firebase.FirebaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FishbowlApplication {
	@Autowired
	FirebaseConfig firebaseConfig;
	public static void main(String[] args) {
		SpringApplication.run(FishbowlApplication.class, args);
	}

}
