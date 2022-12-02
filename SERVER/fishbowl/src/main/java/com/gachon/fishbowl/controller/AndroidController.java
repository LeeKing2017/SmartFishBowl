package com.gachon.fishbowl.controller;

import com.gachon.fishbowl.dto.*;
import com.gachon.fishbowl.entity.*;
import com.gachon.fishbowl.entity.role.Role;
import com.gachon.fishbowl.jwt.TokenProvider;
import com.gachon.fishbowl.service.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.PasswordAuthentication;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@Controller
public class AndroidController {

    private final TokenProvider tokenProvider;
    private final UserIdService userIdService;
    private final UserDeviceService userDeviceService;
    private final UserSetService userSetService;
    private final UserSetFoodTimeService userSetFoodTimeService;
    private final SensingService sensingService;
    private final DeviceIdService deviceIdService;

    @PostMapping("/login")
    ResponseEntity<String> responseJwtToken(@RequestBody LoginDto loginDto) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴
        String KAKAO_USERINFO_REQUEST_URL="https://kapi.kakao.com/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + loginDto.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            log.info("response : {}", response);
            log.info("response.getBody() : {}",response.getBody());
            JSONObject jsonObject = new JSONObject(response.getBody());

            JSONObject kakao_account = jsonObject.getJSONObject("kakao_account");
            String email = kakao_account.getString("email");
            log.info("email : {}",email);

            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(Role.USER.toString());
            if (!userIdService.isRegisteredUsers(email)) {
                log.info("회원가입 시작");
                UserId user = UserId.builder()
                        .role(Role.USER)
                        .id(email)
                        .pw(new BCryptPasswordEncoder().encode(email))
                        .fireBaseToken(loginDto.getFirebaseToken())
                        .build();

                userIdService.saveUser(user);

                User createUser = new User(email, "", Collections.singleton(simpleGrantedAuthority));
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(createUser, loginDto.getAccessToken(), Collections.singleton(simpleGrantedAuthority));
                String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
                log.info("token 발급: {}",token);
                return new ResponseEntity<>(token,HttpStatus.OK);
            }else {
                log.info("이미 등록된 회원");
                if (!userIdService.getUserId(email).get().getFireBaseToken().equals(loginDto.getFirebaseToken())) { //파이어베이스 토큰 다르면 업데이트
                    log.info("firebaseToken Update");
                    UserId userId = userIdService.getUserId(email).get();
                    userId.setFireBaseToken(loginDto.getFirebaseToken());
                    userIdService.saveUser(userId);
                }
                User createUser = new User(email, "", Collections.singleton(simpleGrantedAuthority));
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(createUser, loginDto.getAccessToken(), Collections.singleton(simpleGrantedAuthority));
                String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
                log.info("token 발급: {}",token);
                return new ResponseEntity<>(token,HttpStatus.OK);
            }
        } catch (HttpClientErrorException e) {
            log.error("access token err : {}",e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update/deviceid")
    ResponseEntity<String> updateDeviceId(@RequestBody UpdateDeviceId updateDeviceId) {
        log.info("updateDeviceId.toString() : {}",updateDeviceId.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication.getName() : {}",authentication.getName());
        String email = authentication.getName();

        if (!userDeviceService.isPresentMatchedEmailWithDeviceId(email, updateDeviceId.getDeviceId())) {
            log.info("저장");
            Optional<UserId> userId = userIdService.getUserId(email);
            Optional<DeviceId> deviceId = deviceIdService.getDeviceId(updateDeviceId.getDeviceId());
            UserDevice build = UserDevice.builder().deviceId(deviceId.get())
                    .userId(userId.get())
                    .build();
            userDeviceService.saveUserDevice(build);
        }


        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/setFeedTime")//회원 체크 후 먹이시간 세팅값 받는 거
    ResponseEntity<String> receiveFeedTime(FeedTimeDto feedTimeDto) {
        log.info("feedTimeDto.toString() : {}",feedTimeDto.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("authentication.getName() : {}",authentication.getName());
        if (!userDeviceService.isMatchedEmailWithDeviceId(userEmail, feedTimeDto.getDeviceId())) { //사용자 email과 전달받은 devicId가 연결되어있지 않으면
            return new ResponseEntity<>("{msg : 등록되지 않은 deviceid입니다.}", HttpStatus.BAD_REQUEST);
        }

        if (userSetService.getUserSet(feedTimeDto.getDeviceId()).isEmpty()) { //userSet이 저장되어 있지 않으면 일단 널로 저장
            log.info("userSet 테이블이 없어 빈 테이블을 생성합니다.");
            UserSet build = UserSet.builder().deviceId(DeviceId.builder().id(feedTimeDto.getDeviceId()).build())
                    .userSetPh(null)
                    .userSetTurbidity(null)
                    .userSetWaterLevel(null)
                    .userSetTemperature(null)
                    .build();
            userSetService.saveUserSet(build);

        }

        log.info("{}이 먹이시간 설정 메서드 호출", userEmail);
        if (feedTimeDto.getFirstTime() != null && feedTimeDto.getNumberOfFirstFeedings() != null) {
            userSetFoodTimeService.setFoodTimeAndCnt(feedTimeDto.getDeviceId(), feedTimeDto.getFirstTime(), feedTimeDto.getNumberOfFirstFeedings());
        }
        if (feedTimeDto.getSecondTime() != null && feedTimeDto.getNumberOfSecondFeedings() != null) {
            userSetFoodTimeService.setFoodTimeAndCnt(feedTimeDto.getDeviceId(), feedTimeDto.getSecondTime(), feedTimeDto.getNumberOfSecondFeedings());
        }
        if (feedTimeDto.getThirdTime() != null && feedTimeDto.getNumberOfThirdFeedings() != null) {
            userSetFoodTimeService.setFoodTimeAndCnt(feedTimeDto.getDeviceId(), feedTimeDto.getThirdTime(), feedTimeDto.getNumberOfThirdFeedings());
        }

        return new ResponseEntity<>("먹이 지급 설정이 완료되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/setUserSet")
        //온도 탁도 ph 블라블라 묶어서 세팅값 받는 거
    ResponseEntity<String> setUserSet(UserSetDTO userSetDTO) {
        log.info("userSetDTO.toString() : {}",userSetDTO.toString());
        log.info("SecurityContextHolder.getContext().getAuthentication().getName() : {}",SecurityContextHolder.getContext().getAuthentication().getName());
        Optional<DeviceId> deviceId = deviceIdService.getDeviceId(userSetDTO.getDeviceId());
        UserSet build = UserSet.builder().userSetTemperature(userSetDTO.getTemperature())
                .userSetTurbidity(userSetDTO.getTurbidity())
                .userSetWaterLevel(userSetDTO.getWaterLevel())
                .userSetPh(userSetDTO.getPh())
                .deviceId(deviceId.get())
                .build();
        userSetService.saveUserSet(build);
        return new ResponseEntity<>("사용자가 설정한 수치가 등록되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/getSensingData")
        //회원 체크 후 해당 기기의 가장 최근 아두이노 센싱 데이터 리턴 - 묶어서
    ResponseEntity<ResponseAppSensingDto> getSensingData(GetSensingDto getSensingDto) {
        log.info("getSensingDto.toString() : {}",getSensingDto.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("authentication.getName() : {}",authentication.getName());
        if (!userDeviceService.isMatchedEmailWithDeviceId(userEmail, getSensingDto.getDeviceId())) { //사용자 email과 전달받은 devicId가 연결되어있지 않으면
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Sensing> sensingData = sensingService.getSensingData(getSensingDto.getDeviceId());
        ResponseAppSensingDto build = ResponseAppSensingDto.builder().measuredTemperature(sensingData.get().getMeasuredTemperature())
                .measuredPh(sensingData.get().getMeasuredPh())
                .measuredTurbidity(sensingData.get().getMeasuredTurbidity())
                .measuredWaterLevel(sensingData.get().getMeasuredWaterLevel())
                .build();
        return new ResponseEntity<>(build, HttpStatus.OK);
    }

    @PostMapping("/signout") //탈퇴
    ResponseEntity<String> signOut(SignOutDto signOutDto) {
        log.info(signOutDto.toString());
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>("탈퇴되었습니다",HttpStatus.OK);
    }




}
