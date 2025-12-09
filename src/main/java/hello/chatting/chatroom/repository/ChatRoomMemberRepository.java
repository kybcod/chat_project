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

    @Query(value = """
        SELECT 
            r.id,
            r.room_name,
            r.type,
            u.login_id,
            u.name,
            u.email,
            u.profile_image
        FROM chat_room r
        JOIN chat_room_member m ON r.id = m.room_id
        JOIN user u ON m.user_id = u.login_id
        WHERE r.id IN (
            SELECT room_id
            FROM chat_room_member
            WHERE user_id IN (:userIds)
            GROUP BY room_id
            HAVING COUNT(DISTINCT user_id) = :userCount
        )
    """, nativeQuery = true)
    List<Object[]> findRoomAndUsersByExactMembers(
            @Param("userIds") List<String> userIds,
            @Param("userCount") long userCount
    );



}

