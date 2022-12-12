package com.gachon.fishbowl.controller;

import com.gachon.fishbowl.dto.*;
import com.gachon.fishbowl.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestController {

    @PostMapping("/test/update/deviceid")
    ResponseEntity<String> testupdateDeviceId(@RequestBody UpdateDeviceId updateDeviceId) {
        log.info("updateDeviceId.toString() : {}",updateDeviceId.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication.getName() : {}",authentication.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/test/setFeedTime")//회원 체크 후 먹이시간 세팅값 받는 거
    ResponseEntity<String> testreceiveFeedTime(FeedTimeDto feedTimeDto) {
        log.info("feedTimeDto.toString() : {}",feedTimeDto.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("authentication.getName() : {}",authentication.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/test/setUserSet")
        //온도 탁도 ph 블라블라 묶어서 세팅값 받는 거
    ResponseEntity<String> testsetUserSet(UserSetDTO userSetDTO) {
        log.info("userSetDTO.toString() : {}",userSetDTO.toString());
        log.info("SecurityContextHolder.getContext().getAuthentication().getName() : {}",SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/test/getSensingData")
        //회원 체크 후 해당 기기의 가장 최근 아두이노 센싱 데이터 리턴 - 묶어서
    ResponseEntity<ResponseAppSensingDto> testgetSensingData(GetSensingDto getSensingDto) {
        log.info("getSensingDto.toString() : {}",getSensingDto.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("authentication.getName() : {}",authentication.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
