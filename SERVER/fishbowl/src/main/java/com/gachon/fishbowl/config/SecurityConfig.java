package com.gachon.fishbowl.config;

import com.gachon.fishbowl.jwt.*;
import com.gachon.fishbowl.oauth2.CustomOath2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpSession;

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
                .mvcMatchers("/api/authenticate").permitAll()
                .mvcMatchers("/api/signup").permitAll()
                .mvcMatchers("/oauth2/authorization/google").permitAll()
                .mvcMatchers("/","/test/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()

                .oauth2Login()
                .loginPage("/oauth2/authorization/google")
                .defaultSuccessUrl("/")
//                .redirectionEndpoint()
//                .baseUri("/")


                .userInfoEndpoint().userService(customOath2UserService)

                .and().successHandler(customOAuth2SuccessHandler)

                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)

                .and()
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


}
