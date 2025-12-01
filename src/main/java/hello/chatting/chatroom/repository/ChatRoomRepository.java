package hello.chatting.chatroom.repository;

import hello.chatting.chatroom.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
    SELECT r FROM ChatRoom r
        JOIN ChatRoomMember m1 ON r.id = m1.roomId
        JOIN ChatRoomMember m2 ON r.id = m2.roomId
    WHERE r.type = 'PRIVATE'
      AND m1.userId = :me
      AND m2.userId = :friendId
    """)
    ChatRoom findPrivateRoom(String me, String friendId);
}

