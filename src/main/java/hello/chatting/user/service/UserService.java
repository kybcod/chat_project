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

    public List<User> findAll() throws Exception {
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty() || userList == null) {
            throw new Exception("친구 목록 불러오기를 실패했습니다.");
        }
        return userList;
    }


    // roomName에서 친구 이름 추출하는 메서드
    public String extractFriendName(String roomName, String userId) {
        String[] loginIds = roomName.split(", ");

        for (String loginId : loginIds) {
            if (!loginId.equals(userId)) {
                // 유일한 loginId로 정확한 사용자 조회 가능
                User user = userRepository.findByLoginId(loginId)
                        .orElse(null);
                if (user != null) {
                    return user.getName(); // 프론트에 보여줄 이름
                }
            }
        }
        return null;
    }

}
