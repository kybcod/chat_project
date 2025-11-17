package hello.chatting.user.service;

import hello.chatting.user.domain.User;
import hello.chatting.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public List<User> findAll() {
        return userMapper.findAll();
    }
}
