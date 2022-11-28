package com.gachon.fishbowl.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseService {
    public String sendPhMessage123(String registrationToken, String Ph, String deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("로켓단") // 알림 제목
                                .setBody("귀하의 안드로이드는 해킹되었다.") // 알림 본문
                                .build())
                        .build())
                .setToken(registrationToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }
    public String sendTemperatureMessage(String registrationToken, String temperature, String deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔") // 알림 제목
                                .setBody(deviceId+"어항의 현재 온도가 "+temperature+"도입니다") // 알림 본문
                                .build())
                        .build())
                .setToken(registrationToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendWaterLevelMessage(String registrationToken, String WaterLevel, String deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔") // 알림 제목
                                .setBody(deviceId+"어항의 현재 물 수위가 설정한 "+WaterLevel+"입니다") // 알림 본문
                                .build())
                        .build())
                .setToken(registrationToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendPhMessage(String registrationToken, String Ph, String deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔") // 알림 제목
                                .setBody(deviceId+"어항의 현재 Ph값이 "+Ph+"입니다") // 알림 본문
                                .build())
                        .build())
                .setToken(registrationToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendTurbidityMessage(String registrationToken, String Turbidity, String deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔") // 알림 제목
                                .setBody(deviceId+"어항의 현재 탁도가 "+Turbidity+"입니다") // 알림 본문
                                .build())
                        .build())
                .setToken(registrationToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

    public String sendLeftoversMessage(String registrationToken, String deviceId) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setRestrictedPackageName("com.example.smartfishbowl") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("뽀끔뽀끔") // 알림 제목
                                .setBody(deviceId+"어항의 먹이가 부족합니다") // 알림 본문
                                .build())
                        .build())
                .setToken(registrationToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();

        // Send a message to the device corresponding to the provided registration token.
        String response = FirebaseMessaging.getInstance().send(message);

        return response;
    }

}
