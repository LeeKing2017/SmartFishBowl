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
    private final String registrationToken = "cP4NvYdrTie6GYgZL9qX_u:APA91bFO4sYsqTJr1pd3mAkKi1bRpq7cbLNLfRWPnrxZqjUhH_i8c2IwTTuCDdriGstra8JFVdN18y0rjiT1zbBbd7QSPqd4GulkcdDoc99QVcxxRGf-rHVT67mvNkYUTS9z9-aKyWEN";
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
