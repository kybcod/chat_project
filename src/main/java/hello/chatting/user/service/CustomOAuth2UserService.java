package hello.chatting.user.service;

import hello.chatting.user.domain.CustomOAuth2User;
import hello.chatting.user.domain.User;
import hello.chatting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google, kakao, naver

        // 소셜 유저 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Provider별 데이터 파싱
        String loginId;
        String email;
        String name;
        String profileImage = null;

        if ("google".equals(registrationId)) {
            loginId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            profileImage = (String) attributes.get("picture");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            loginId = attributes.get("id").toString();
            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
            profileImage = (String) profile.get("profile_image_url");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            loginId = response.get("id").toString();
            email = (String) response.get("email");
            name = (String) response.get("name");
            profileImage = (String) response.get("profile_image");
        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        }

        User user = saveUser(loginId, email, name, profileImage);

        Map<String, Object> standardizedAttributes = new HashMap<>();
        standardizedAttributes.put("loginId", loginId);
        standardizedAttributes.put("email", email);
        standardizedAttributes.put("name", name);
        standardizedAttributes.put("profileImage", profileImage);
        standardizedAttributes.put("provider", registrationId);


        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                standardizedAttributes,
                "loginId",
                loginId
        );
    }

    private User saveUser(String loginId, String email, String name, String profileImage) {
        return userRepository.findByLoginId(loginId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .loginId(loginId)
                            .email(email)
                            .name(name)
                            .profileImage(profileImage)
                            .role("USER")
                            .password("") // 소셜 로그인용
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
