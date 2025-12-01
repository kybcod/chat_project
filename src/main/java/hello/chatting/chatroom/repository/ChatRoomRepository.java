package hello.chatting.chatroom.repository;

import hello.chatting.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<User, Long> {
}
