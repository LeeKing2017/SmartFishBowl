package com.gachon.fishbowl.controller;

import com.gachon.fishbowl.dto.*;
import com.gachon.fishbowl.entity.*;
import com.gachon.fishbowl.entity.role.Role;
import com.gachon.fishbowl.jwt.TokenProvider;
import com.gachon.fishbowl.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
        String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + loginDto.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            log.info("response : {}", response);
            log.info("response.getBody() : {}", response.getBody());
            JSONObject jsonObject = new JSONObject(response.getBody());

            JSONObject kakao_account = jsonObject.getJSONObject("kakao_account");
            String email = kakao_account.getString("email");
            log.info("email : {}", email);

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
                log.info("token 발급: {}", token);
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
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
                log.info("token 발급: {}", token);
                return new ResponseEntity<>(token, HttpStatus.OK);
            }
        } catch (HttpClientErrorException e) {
            log.error("access token err : {}", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update/deviceid")
    ResponseEntity<String> updateDeviceId(@RequestBody UpdateDeviceId updateDeviceId) {
        log.info("updateDeviceId.toString() : {}", updateDeviceId.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication.getName() : {}", authentication.getName());
        String email = authentication.getName();

        if (!userDeviceService.isPresentMatchedEmailWithDeviceId(email, updateDeviceId.getDeviceId())
                && userDeviceService.getUserDeviceByDeviceId(deviceIdService.getDeviceId(updateDeviceId.getDeviceId()).get()).isEmpty()) {
            log.info("기기와 회원 정보 메핑 후 저장");
            Optional<UserId> userId = userIdService.getUserId(email);
            Optional<DeviceId> deviceId = deviceIdService.getDeviceId(updateDeviceId.getDeviceId());
            UserDevice build = UserDevice.builder().deviceId(deviceId.get())
                    .userId(userId.get())
                    .build();
            userDeviceService.saveUserDevice(build);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.info("이미 사용중인 기기입니다");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }



    }

    @PostMapping("/setFeedTime")
    ResponseEntity<String> receiveFeedTime(@RequestBody FeedTimeDto feedTimeDto) {//회원 체크 후 먹이시간 세팅값 받는 거
        log.info("feedTimeDto.toString() : {}", feedTimeDto.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("authentication.getName() : {}", authentication.getName());
        if (!userDeviceService.isMatchedEmailWithDeviceId(userEmail, feedTimeDto.getDeviceId())) { //사용자 email과 전달받은 devicId가 연결되어있지 않으면
            return new ResponseEntity<>("{msg : 등록되지 않은 deviceid입니다.}", HttpStatus.BAD_REQUEST);
        }


        if (userSetService.getUserSet(deviceIdService.getDeviceId(feedTimeDto.getDeviceId()).get()).isEmpty()) { //userSet이 저장되어 있지 않으면 일단 널로 저장
            log.info("userSet 테이블이 없어 빈 테이블을 생성합니다.");
            UserSet build = UserSet.builder().deviceId(DeviceId.builder().id(feedTimeDto.getDeviceId()).build())
                    .userSetPh(null)
                    .userSetTurbidity(null)
                    .userSetWaterLevel(null)
                    .userSetTemperature(null)
                    .build();
            userSetService.saveUserSet(build);

        }

        log.info("{}이 먹이시간 설정 메서드 호출", userEmail); //처음엔 다 널-> 하나만 넣으면 나머지는 0 -> 초기화하면 다 널
        if (feedTimeDto.getNumberOfFirstFeedings()!= null && feedTimeDto.getNumberOfFirstFeedings() != 0) {
            userSetFoodTimeService.setFirstFoodTimeAndCnt(feedTimeDto.getDeviceId(), feedTimeDto.getFirstTime(), feedTimeDto.getNumberOfFirstFeedings());
        }
        if (feedTimeDto.getNumberOfFirstFeedings() == null) {
            userSetFoodTimeService.setFirstFoodTimeAndCnt(feedTimeDto.getDeviceId(),null,null);
        }
        if (feedTimeDto.getNumberOfSecondFeedings()!= null && feedTimeDto.getNumberOfSecondFeedings() != 0) {
            userSetFoodTimeService.setSecondFoodTimeAndCnt(feedTimeDto.getDeviceId(), feedTimeDto.getSecondTime(), feedTimeDto.getNumberOfSecondFeedings());
        }
        if (feedTimeDto.getNumberOfSecondFeedings() == null) {
            userSetFoodTimeService.setSecondFoodTimeAndCnt((feedTimeDto.getDeviceId()), null, null);
        }
        if (feedTimeDto.getNumberOfThirdFeedings()!= null && feedTimeDto.getNumberOfThirdFeedings() != 0) {
            userSetFoodTimeService.setThirdFoodTimeAndCnt(feedTimeDto.getDeviceId(), feedTimeDto.getThirdTime(), feedTimeDto.getNumberOfThirdFeedings());
        }
        if (feedTimeDto.getNumberOfThirdFeedings() == null) {
            userSetFoodTimeService.setThirdFoodTimeAndCnt((feedTimeDto.getDeviceId()), null, null);
        }

        return new ResponseEntity<>("먹이 지급 설정이 완료되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/setUserSet") //각각의 셋팅값이 0 또는 0.0일때는 null로 저장할 것
    ResponseEntity<String> setUserSet(@RequestBody UserSetDTO userSetDTO) { //온도 탁도 ph 블라블라 묶어서 세팅값 받는 거//디비에 컬럼이 추가되는 오류,컬럼 값 변경으로 수정 요망
        log.info("userSetDTO.toString() : {}", userSetDTO.toString());
        log.info("SecurityContextHolder.getContext().getAuthentication().getName() : {}", SecurityContextHolder.getContext().getAuthentication().getName());
        if (userSetDTO.getPh().compareTo(0.0) == 0) {
            userSetDTO.setPh(null);
        }
        if (userSetDTO.getTemperature().compareTo(0.0) == 0) {
            userSetDTO.setTemperature(null);
        }
        if (userSetDTO.getTurbidity().compareTo(0.0) == 0) {
            userSetDTO.setTurbidity(null);
        }
        if (userSetDTO.getWaterLevel() == 0) {
            userSetDTO.setWaterLevel(null);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserId> userId = userIdService.getUserId(authentication.getName());


//        Optional<UserDevice> userDeviceByUserId = userDeviceService.getUserDeviceByUserId(userId.get());

        Optional<DeviceId> deviceId = deviceIdService.getDeviceId(userSetDTO.getDeviceId());
        Optional<UserDevice> userDeviceByDeviceId = userDeviceService.getUserDeviceByDeviceId(deviceId.get());

        Optional<UserSet> userSet = userSetService.getUserSet(userDeviceByDeviceId.get().getDeviceId());
        if (userSet.isEmpty()) {
            log.info("사용자 설정 값 새로 등록");
            UserSet build = UserSet.builder().userSetTemperature(userSetDTO.getTemperature())
                    .userSetTurbidity(userSetDTO.getTurbidity())
                    .userSetWaterLevel(userSetDTO.getWaterLevel())
                    .userSetPh(userSetDTO.getPh())
                    .deviceId(userDeviceByDeviceId.get().getDeviceId())
                    .build();
            userSetService.saveUserSet(build);
            return new ResponseEntity<>("사용자가 설정한 수치가 등록되었습니다.", HttpStatus.OK);
        }else {
            log.info("사용자 설정 변경");
            userSet.get().setUserSetPh(userSetDTO.getPh());
            userSet.get().setUserSetTemperature(userSetDTO.getTemperature());
            userSet.get().setUserSetTurbidity(userSetDTO.getTurbidity());
            userSet.get().setUserSetWaterLevel(userSetDTO.getWaterLevel());
            userSetService.saveUserSet(userSet.get());
            return new ResponseEntity<>("사용자가 설정한 수치가 등록되었습니다.", HttpStatus.OK);
        }


    }

    @PostMapping("/getSensingData")
    ResponseEntity<ResponseAppSensingDto> getSensingData(@RequestBody GetSensingDto getSensingDto) {//회원 체크 후 해당 기기의 가장 최근 아두이노 센싱 데이터 리턴 - 묶어서
        log.info("getSensingDto.toString() : {}", getSensingDto.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("authentication.getName() : {}", authentication.getName());
        if (!userDeviceService.isMatchedEmailWithDeviceId(userEmail, getSensingDto.getDeviceId())) { //사용자 email과 전달받은 devicId가 연결되어있지 않으면
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (sensingService.getSensingData(getSensingDto.getDeviceId()).isPresent()) {
            Optional<Sensing> sensingData = sensingService.getSensingData(getSensingDto.getDeviceId());
            ResponseAppSensingDto build = ResponseAppSensingDto.builder().measuredTemperature(sensingData.get().getMeasuredTemperature())
                    .measuredPh(sensingData.get().getMeasuredPh())
                    .measuredTurbidity(sensingData.get().getMeasuredTurbidity())
                    .measuredWaterLevel(sensingData.get().getMeasuredWaterLevel())
                    .build();
            log.info("센싱 데이터 리턴");
            return new ResponseEntity<>(build, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping ("/signout")
    ResponseEntity<String> signOut() {//탈퇴
        log.info("탈퇴 로직 실행");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserId> userId = userIdService.getUserId(email);
//        Optional<UserDevice> userDeviceByUserId = userDeviceService.getUserDeviceByUserId(userId.get());
//        DeviceId deviceId = userDeviceByUserId.get().getDeviceId();
//        userSetFoodTimeService.deleteFoodTimeAndCnt(deviceId.getId()); //먹이 배급 테이블 삭제

        Optional<List<UserDevice>> allUserDeviceByUserId = userDeviceService.getAllUserDeviceByUserId(userId.get());
//        allUserDeviceByUserId.get().iterator().forEachRemaining(e->userSetFoodTimeService.deleteFoodTimeAndCnt(e.getDeviceId().getId()));

        Iterator<UserDevice> iterator = allUserDeviceByUserId.get().iterator();

        while (iterator.hasNext()) {
            UserDevice next = iterator.next();
            userSetFoodTimeService.deleteFoodTimeAndCnt(next.getDeviceId().getId());
            Optional<UserSet> userSet = userSetService.getUserSet(next.getDeviceId());
            if (userSet.isPresent()) {
                userSetService.deleteUserSet(userSet.get()); //userset삭제
            }else {
                log.info("userSet 없어서 삭제 안함");
            }

//            Optional<UserDevice> userDeviceByDeviceId = userDeviceService.getUserDeviceByDeviceId(iterator.next().getDeviceId());
            userDeviceService.deleteUserDevice(next); //userdevice 삭제
        }
        userIdService.deleteUser(userId.get()); //userId 삭제
        return new ResponseEntity<>("탈퇴되었습니다", HttpStatus.OK);
    }

    @GetMapping("/test")
    ResponseEntity<String> test() {
        String email = "jw1010110@naver.com";
        Optional<UserId> userId = userIdService.getUserId(email);
        Optional<UserDevice> userDeviceByUserId = userDeviceService.getUserDeviceByUserId(userId.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}

