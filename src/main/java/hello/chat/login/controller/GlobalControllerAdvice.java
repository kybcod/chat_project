package hello.chat.login.controller;

import hello.chat.login.domain.CustomOAuth2User;
import hello.chat.login.domain.User;
import hello.chat.login.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final LoginMapper loginMapper;

    @ModelAttribute("loginUser")
    public User loginUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomOAuth2User customUser) {
                Map<String, Object> response = (Map<String, Object>) ((Map<String, Object>) customUser.getAttributes().get("response"));
                return User.builder()
                        .loginId(customUser.getName())
                        .name((String) response.get("name"))
                        .profileImage((String) response.get("profile_image"))
                        .build();
            }

        }
        return null;
    }

}

