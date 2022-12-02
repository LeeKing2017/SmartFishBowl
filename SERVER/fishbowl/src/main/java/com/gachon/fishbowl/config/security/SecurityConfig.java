package com.gachon.fishbowl.config.security;

import com.gachon.fishbowl.config.security.CustomOAuth2SuccessHandler;
import com.gachon.fishbowl.entity.role.Role;
import com.gachon.fishbowl.jwt.*;
import com.gachon.fishbowl.oauth2.CustomOath2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity //기본적인 웹보안 실행
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;


    private final CustomOath2UserService customOath2UserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

                .authorizeRequests()
                .mvcMatchers("/","/login","/bowl","/sendMessageTest123","/sendMessageTest1","/sendMessageTest2","/sendMessageTest3","/sendMessageTest4","/sendMessageTest5","/test").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


}
