package hello.chat.config;

import hello.chat.login.service.CustomOAuth2UserService;
import hello.chat.login.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    // http://localhost:8087/oauth2/authorization/kakao
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/chatting", "/chatbot","/oauth2/**", "/css/**", "/js/**", "/images/**", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                    .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                )
                .formLogin(login -> login
                        .loginPage("/login") // 내가 만든 login.html 경로
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                );

        return httpSecurity.build();
    }

    // BCrypt password encoder를 리턴하는 메서드
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
