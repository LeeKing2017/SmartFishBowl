package com.gachon.fishbowl.config;

import com.gachon.fishbowl.jwt.JwtFilter;
import com.gachon.fishbowl.jwt.TokenProvider;
import com.gachon.fishbowl.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final CookieUtils cookieUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        super.onAuthenticationSuccess(request, response, chain, authentication);
        log.info("커스텀 성공 핸들러 작동");
        String token = tokenProvider.createToken(authentication);
        log.info("{}",token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer "+ token);

        response.addCookie(cookieUtils.generateJwtHttpOnlyCookie("smartBowlByJungwoo", token, 86400));
        response.addCookie(cookieUtils.generateNormalCookie("smartBowlByJungwoo-flag","true",86400));
        getRedirectStrategy().sendRedirect(request,response,"/");
    }
}
