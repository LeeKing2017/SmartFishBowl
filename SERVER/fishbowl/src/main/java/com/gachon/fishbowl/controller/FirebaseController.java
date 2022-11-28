package com.gachon.fishbowl.controller;

import com.gachon.fishbowl.repository.SensingRepository;
import com.gachon.fishbowl.service.FirebaseService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FirebaseController {
    private final FirebaseService firebaseService;
    private final SensingRepository sensingRepository;
    private final String registrationToken = "dFZky7irRF2YS5V-Biovdb:APA91bFW4yACjB1kInkJrKVyxsYcDDg6WhvViysaOPEepLSlxuuidC5tDL3_TROy0uO2PFjvQqYDzS1PQDQ8SYt-ZCxa8SkbkOulg10xbKz57YKC0a4wOQwT-VowmzmLw6EqbnOy0y65";

    @ResponseBody
    @GetMapping("/sendMessageTest123")
    public String sendTemperatureTest123() throws FirebaseMessagingException, IOException {
        firebaseService.sendPhMessage123(registrationToken, "10","123");

        return "Ok";
    }
    @ResponseBody
    @GetMapping("/sendMessageTest1")
    public String sendTemperatureTest() throws FirebaseMessagingException, IOException {
        firebaseService.sendTemperatureMessage(registrationToken, "10","123");

        return "Ok";
    }

    @ResponseBody
    @GetMapping("/sendMessageTest2")
    public String sendWaterLevelTest() throws FirebaseMessagingException, IOException {
        firebaseService.sendWaterLevelMessage(registrationToken, "10","123");

        return "Ok";
    }
    @ResponseBody
    @GetMapping("/sendMessageTest3")
    public String sendPhTest() throws FirebaseMessagingException, IOException {
        firebaseService.sendPhMessage(registrationToken, "10","123");

        return "Ok";
    }
    @ResponseBody
    @GetMapping("/sendMessageTest4")
    public String sendTurbidityTest() throws FirebaseMessagingException, IOException {
        firebaseService.sendTurbidityMessage(registrationToken, "10","123");

        return "Ok";
    }
    @ResponseBody
    @GetMapping("/sendMessageTest5")
    public String sendMessageTest() throws FirebaseMessagingException, IOException {
        firebaseService.sendLeftoversMessage(registrationToken,"123");

        return "Ok";
    }
}
