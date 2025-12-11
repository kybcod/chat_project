package hello.chatting.user.repository;

import hello.chatting.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    @Query("""
        SELECT u FROM ChatRoom r
            JOIN ChatRoomMember crm ON r.id = crm.roomId
            JOIN User u ON crm.userId = u.loginId
        WHERE r.id = :roomId
    """)
    List<User> findChatRoomUser(Long roomId);
}
