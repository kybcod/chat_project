package hello.chatting.user.service;

import hello.chatting.user.domain.User;
import hello.chatting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }


    // roomName에서 친구 이름 추출하는 메서드
    public String extractFriendName(String roomName, String userId) {
        String[] names = roomName.split(", ");

        for (String name : names) {
            Optional<User> userOpt = userRepository.findByName(name);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!user.getLoginId().equals(userId)) {
                    return name; // userId와 다른 이름 = 친구 이름
                }
            }
        }
        return null;
    }
}
