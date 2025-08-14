package hello.chat.login.controller;

import hello.chat.login.domain.User;
import hello.chat.login.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final LoginMapper loginMapper;

    @ModelAttribute("loginUser")
    public User loginUser(Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            return loginMapper.findUserByLoginId(authentication.getName());
        }

        return null;
    }
}

