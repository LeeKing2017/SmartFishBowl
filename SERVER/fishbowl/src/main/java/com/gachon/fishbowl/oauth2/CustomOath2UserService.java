package com.gachon.fishbowl.oauth2;

import com.gachon.fishbowl.entity.UserId;
import com.gachon.fishbowl.entity.role.Role;
import com.gachon.fishbowl.jwt.TokenProvider;
import com.gachon.fishbowl.repository.UserIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class CustomOath2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserIdRepository userIdRepository;

    /**
     * 구글 로그인 클릭 -> 구글 로그인 창 -> 로그인 완료 시 code리턴 -> OAuth2 client라이브러리가 Acess Token 요청 -> Acess Token을 받으면 이것이 userRequest임
     * userRequest로 회원 프로필을 받음(이때 loadUser 사용) -> 회원 프로필 받음
     */
    @Override //google로 받은 userRequest data handling
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOath2UserService시작: {}", super.loadUser(userRequest).getAttributes());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String resourceServerUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        String accessToken = userRequest.getAccessToken().getTokenValue();

        OAuth2User oAuth2User = super.loadUser(userRequest);

        if (userIdRepository.findById(oAuth2User.getAttributes().get("email").toString()).isEmpty()) {
            log.info("저장메서드 실행 {}",oAuth2User.getAttributes());
            userIdRepository.save(
                    UserId.builder()
                            .id(oAuth2User.getAttribute("email").toString())
                            .pw(passwordEncoder.encode(oAuth2User.getAttribute("sub").toString()))
                            .role(Role.USER)
                            .build()

            );


        }
        OAuth2User user = null;
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Map<String, Object> attributes = new HashMap<>();

        if (resourceServerUri != null && !"".equals(resourceServerUri)
                && accessToken != null && !"".equals(accessToken)){

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            log.info("headers : {}",headers);
            log.info("headers.get(\"Authorization\") : {}",headers.get("Authorization"));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

            attributes.put("email",oAuth2User.getAttribute("email").toString());
            attributes.put("sub",oAuth2User.getAttribute("sub").toString());

            user = new DefaultOAuth2User(authorities, attributes,"email");
            log.info("user : {}",user);
        }
        return user;
    }
}
