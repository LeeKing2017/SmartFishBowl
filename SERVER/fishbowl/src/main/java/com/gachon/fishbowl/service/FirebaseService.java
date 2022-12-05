package com.gachon.fishbowl.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseService {
    public String sendTemperatureLowMessage(String firebaseToken, Double temperature, Long deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔 온도") // 알림 제목
                                .setBody(deviceId+"어항의 현재 온도가 "+temperature+"도입니다. 발열 패드를 작동시켰습니다.") // 알림 본문
                                .build())
                        .build())
                .setToken(firebaseToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendTemperatureHighMessage(String firebaseToken, Double temperature, Long deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔 온도") // 알림 제목
                                .setBody(deviceId+"어항의 현재 온도가 "+temperature+"도입니다. 쿨러를 작동시켰습니다.") // 알림 본문
                                .build())
                        .build())
                .setToken(firebaseToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendWaterLevelLowMessage(String firebaseToken, Integer WaterLevel, Long deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔 수위") // 알림 제목
                                .setBody(deviceId+"어항의 현재 물 수위가 설정한 물 수위보다 낮은 "+WaterLevel+"입니다. 물을 채워주세요") // 알림 본문
                                .build())
                        .build())
                .setToken(firebaseToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendPhLowMessage(String firebaseToken, Double Ph, Long deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔 ph") // 알림 제목
                                .setBody(deviceId+"어항의 현재 Ph값이 설정값보다 낮은 "+Ph+"입니다.") // 알림 본문
                                .build())
                        .build())
                .setToken(firebaseToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendPhHighMessage(String firebaseToken, Double Ph, Long deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔 ph") // 알림 제목
                                .setBody(deviceId+"어항의 현재 Ph값이 설정 값보다 높은"+Ph+"입니다.") // 알림 본문
                                .build())
                        .build())
                .setToken(firebaseToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }
    public String sendTurbidityMessage(String firebaseToken, Double Turbidity, Long deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔 탁도") // 알림 제목
                                .setBody(deviceId+"어항의 현재 탁도가 "+Turbidity+"로 물이 더럽습니다. 물을 갈아주세요") // 알림 본문
                                .build())
                        .build())
                .setToken(firebaseToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendLeftoversMessage(String firebaseToken, Long deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔 먹이") // 알림 제목
                                .setBody(deviceId+"어항의 먹이가 부족합니다. 먹이를 채워주세요") // 알림 본문
                                .build())
                        .build())
                .setToken(firebaseToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

}