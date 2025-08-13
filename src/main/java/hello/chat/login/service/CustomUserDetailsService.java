package hello.chat.login.service;

import hello.chat.login.domain.CustomUserDetails;
import hello.chat.login.domain.User;
import hello.chat.login.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginMapper loginMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = loginMapper.findUserByLoginId(username);

        if (user != null) {
            return new CustomUserDetails(user);
        }

        return null;
    }
}
