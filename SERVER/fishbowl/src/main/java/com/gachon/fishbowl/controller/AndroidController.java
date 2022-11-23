package com.gachon.fishbowl.controller;

import com.gachon.fishbowl.dto.LoginDto;
import com.gachon.fishbowl.dto.TokenDto;
import com.gachon.fishbowl.jwt.JwtFilter;
import com.gachon.fishbowl.jwt.TokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@Controller
public class AndroidController {

    private final TokenProvider tokenProvider;

    @GetMapping("/oauth2/callback")
    String calback() {
        return "/oauth2/callback";
    }



//    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/")
    ResponseEntity<TokenDto> index(@AuthenticationPrincipal OAuth2User oAuth2User) {
        log.info("index 진입");

        log.info("{}", SecurityContextHolder.getContext().getAuthentication());

        log.info("oAuth2User : {}",oAuth2User);

        String token = tokenProvider.createToken(SecurityContextHolder.getContext().getAuthentication());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer "+ token);
//        return token;
        return new ResponseEntity<>(new TokenDto(token), httpHeaders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/test/test")
    ResponseEntity<String> test(@RequestBody TokenDto tokenDto) {
        log.info("test 진입성공");
        if (!tokenProvider.validateToken(tokenDto.getToken())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Authentication authentication = tokenProvider.getAuthentication(tokenDto.getToken());
        log.info("authentication.getAuthorities().toString() : {}", authentication.getAuthorities().toString());
        if (authentication.getAuthorities().toString().equals("[ROLE_USER]")){
            return new ResponseEntity<>("okoko", HttpStatus.OK);
        }
        return new ResponseEntity<>("nono", HttpStatus.BAD_REQUEST);
    }
    //회원 체크 후 먹이시간 세팅값 받는 거

    //온도 탁도 ph 블라블라 묶어서 세팅값 받는 거 //post

    //회원 체크 후 해당 기기의 가장 최근 아두이노 센싱 데이터 리턴 - 묶어서


    private final AuthenticationManagerBuilder authenticationManagerBuilder;


}
