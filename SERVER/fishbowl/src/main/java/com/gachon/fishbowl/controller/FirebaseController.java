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
    private final String registrationToken = "eYMoV6McTUiJtACu_xc9MZ:APA91bF1K7i-uC3_rjBP3EaDDJDNNzUhjMH1fMYvLPgoA8PGoeFMiGZsslFMu2PvgaW07IaQURYmzFpQibZvi_AYxhnspRc57uUvZ8PsGX2bHOaONWNi2LfWpOHzAimmC-0C3_SYo_lm";

}
