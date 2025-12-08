package hello.chatting.chatroom.repository;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    ChatRoomMember findByRoomIdAndUserIdNot(Long roomId, String userId);

    @Query("""
        SELECT r
        FROM ChatRoom r
        WHERE r.id IN (
            SELECT m.roomId
            FROM ChatRoomMember m
            WHERE m.userId IN :userIds
            GROUP BY m.roomId
            HAVING COUNT(DISTINCT m.userId) = :userCount
        )
    """)
    List<ChatRoom> findRoomsByExactMembers(@Param("userIds") List<String> userIds,
                                           @Param("userCount") long userCount);


}

