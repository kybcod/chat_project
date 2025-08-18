package hello.chat.config;

import hello.chat.login.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private static final String[] WHITELIST = {"/", "/oauth2/**", "/css/**", "/js/**", "/images/**", "/login"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {


        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST).permitAll() // 인증안해도 되는 url
                        .anyRequest().authenticated()
                )
                // 일반 폼(내가 만든) 로그인 설정
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*")) // OAuth2 콜백 URL 패턴 설정
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // 사용자 정보를 가져올 때 커스텀 서비스 사용
                        .defaultSuccessUrl("/", true) // 로그인 성공 후 리다이렉트할 URL
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // 로그아웃 성공 후 이동할 URL 지정
                        .invalidateHttpSession(true) // 로그아웃 시 세션 무효화
                        .deleteCookies("JSESSIONID") // 로그아웃 시 쿠키 JSESSIONID 삭제
                )
                .csrf(csrf -> csrf.disable()); // CSRF 보호 비활성화

        return httpSecurity.build();
    }

    // BCrypt password encoder를 리턴하는 메서드
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
