package com.gachon.fishbowl.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.gachon.fishbowl.dto.*;
import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.Sensing;
import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.entity.UserSet;
import com.gachon.fishbowl.entity.role.Role;
import com.gachon.fishbowl.jwt.JwtFilter;
import com.gachon.fishbowl.jwt.TokenProvider;
import com.gachon.fishbowl.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.JSONParser;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
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

//    @GetMapping("/")
//    ResponseEntity<TokenDto> index(@AuthenticationPrincipal OAuth2User oAuth2User) {
//        log.info("index 진입");
//
//        log.info("{}", SecurityContextHolder.getContext().getAuthentication());
//
//        log.info("oAuth2User : {}",oAuth2User);
//
//        String token = tokenProvider.createToken(SecurityContextHolder.getContext().getAuthentication());
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer "+ token);
////        return token;
//        return new ResponseEntity<>(new TokenDto(token), httpHeaders, HttpStatus.OK);
//    }

    @GetMapping("/") //test용 access token 발급 용
    ResponseEntity<TokenDto> index(@AuthenticationPrincipal OAuth2User oAuth2User) {
        log.info("index 진입");
        return new ResponseEntity<>(HttpStatus.OK);

//        log.info("{}", SecurityContextHolder.getContext().getAuthentication());
//
//        log.info("oAuth2User : {}",oAuth2User);
//
//        String token = "ya29.a0AeTM1ietLKTeN87bibmCoHsk5mkLnAtKgzsVxbjKPAcogT8WoHSPz2qpyvYmdWtY_AIemIO9Q9FYmC6pKiX3LfM3Q_2zhOEeOlqqBe0sc2mvpLDaTugE8pg9cLKwCDJCXyiFW8Oi-31-RNGdyfcO3CDF3cPtaCgYKAfISARESFQHWtWOmoRwPI5gp8SVs3ba2bt552A0163";
//        String GOOGLE_USERINFO_REQUEST_URL="https://www.googleapis.com/oauth2/v1/userinfo";
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Authorization","Bearer "+token);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(httpHeaders);
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
//        log.info("response : {}", response.getBody());
//
//
//        return new ResponseEntity<>(new TokenDto(token,""), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/login")
    ResponseEntity<String> responseJwtToken(@RequestBody LoginDto loginDto) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴
        String accessToken = loginDto.getAccessToken();
        String KAKAO_USERINFO_REQUEST_URL="https://kapi.kakao.com/v1/user/logout";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + "nAonNj15wi_YLq6Jne4qGwcptkX0Uq1emn1yycUpCinJXwAAAYS-4yxF");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
//        try {
            ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            log.info("response : {}", response);
            log.info("response.getBody() : {}",response.getBody());
            JSONObject jsonObject = new JSONObject(response.getBody());

//            String email = jsonObject.getString("email");
//            log.info("email : {}",email);
//
//            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(Role.USER.toString());
//            if (!userIdService.isRegisteredUsers(email)) {
//                UserId user = UserId.builder()
//                        .role(Role.USER)
//                        .id(email)
//                        .pw(new BCryptPasswordEncoder().encode(jsonObject.getString("id")))
//                        .fireBaseToken(tokenDto.getFireBaseToken())
//                        .build();
//
//                userIdService.saveUser(user);
//
//                User createUser = new User(email, "", Collections.singleton(simpleGrantedAuthority));
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(createUser, tokenDto.getAccessToken(), Collections.singleton(simpleGrantedAuthority));
//                String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
//                return new ResponseEntity<>(token,HttpStatus.OK);
//            }else {
//                User createUser = new User(email, "", Collections.singleton(simpleGrantedAuthority));
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(createUser, tokenDto.getAccessToken(), Collections.singleton(simpleGrantedAuthority));
//                String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
//                return new ResponseEntity<>(token,HttpStatus.OK);
//            }
//
//
//        } catch (HttpClientErrorException e) {
//            log.error("access token err : {}",e.getMessage());
//        }



//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @PreAuthorize("hasRole('ROLE_USER')")
//    @PostMapping("/test/test")
//    ResponseEntity<String> test(@RequestBody TokenDto tokenDto) {
//        log.info("test 진입성공");
//        if (!tokenProvider.validateToken(tokenDto.getAccessToken())) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        Authentication authentication = tokenProvider.getAuthentication(tokenDto.getAccessToken());
//        log.info("authentication.getAuthorities().toString() : {}", authentication.getAuthorities().toString());
//        log.info("{}",authentication.getPrincipal());
//        log.info("{}",authentication.getName());
//        if (authentication.getAuthorities().toString().equals("[ROLE_USER]")){
//            return new ResponseEntity<>("okoko", HttpStatus.OK);
//        }
//        return new ResponseEntity<>("nono", HttpStatus.BAD_REQUEST);
//    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/setFeedTime") //회원 체크 후 먹이시간 세팅값 받는 거
    ResponseEntity<String> receiveFeedTime(FeedTimeDto feedTimeDto) {
        if (!tokenProvider.validateToken(feedTimeDto.getJwtToken())) { //토큰 검증
            return new ResponseEntity<>("BAD_REQUEST", HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = tokenProvider.getAuthentication(feedTimeDto.getJwtToken()); //토큰에서 사용자 정보 추출
        String userEmail = authentication.getName();
        if (!userDeviceService.isMatchedEmailWithDeviceId(userEmail,feedTimeDto.getDeviceId())) { //사용자 email과 전달받은 devicId가 연결되어있지 않으면
            return new ResponseEntity<>("{msg : 등록되지 않은 deviceid입니다.}",HttpStatus.BAD_REQUEST);
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

        log.info("{}이 먹이시간 설정 메서드 호출",userEmail);
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/setUserSet")  //온도 탁도 ph 블라블라 묶어서 세팅값 받는 거
    ResponseEntity<String> setUserSet(UserSetDTO userSetDTO) {
        if (!tokenProvider.validateToken(userSetDTO.getJwtToken())) { //토큰 검증
            return new ResponseEntity<>("BAD_REQUEST", HttpStatus.BAD_REQUEST);
        }
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/getSensingData") //회원 체크 후 해당 기기의 가장 최근 아두이노 센싱 데이터 리턴 - 묶어서
    ResponseEntity<Sensing> getSensingData(GetSensingDto getSensingDto) {
        if (!tokenProvider.validateToken(getSensingDto.getJwtToken())) { //토큰 검증
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = tokenProvider.getAuthentication(getSensingDto.getJwtToken()); //토큰에서 사용자 정보 추출
        String userEmail = authentication.getName();
        if (!userDeviceService.isMatchedEmailWithDeviceId(userEmail,getSensingDto.getDeviceId())) { //사용자 email과 전달받은 devicId가 연결되어있지 않으면
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Sensing> sensingData = sensingService.getSensingData(getSensingDto.getDeviceId());
        return new ResponseEntity<>(sensingData.get(), HttpStatus.OK);
    }




}
