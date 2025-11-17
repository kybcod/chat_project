package hello.chatting.user.controller;

import hello.chatting.user.domain.CustomOAuth2User;
import hello.chatting.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    @ModelAttribute("loginUser")
    public User loginUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomOAuth2User customUser) {
                Map<String, Object> response = (Map<String, Object>) customUser.getAttributes().get("response");
                return User.builder()
                        .loginId(customUser.getName())
                        .email((String) response.get("email"))
                        .name((String) response.get("name"))
                        .role((String) response.get("role"))
                        .profileImage((String) response.get("profile_image"))
                        .build();
            }

        }
        return null;
    }

}

