package hello.chatting.chat.repository;

import hello.chatting.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
        SELECT m
        FROM ChatMessage m
        JOIN ChatRoomMember crm
          ON m.roomId = crm.roomId
        WHERE m.roomId = :roomId
          AND crm.userId = :userId
          AND (
               crm.activeAt IS NULL
               OR m.createdAt > crm.activeAt
          )
        ORDER BY m.createdAt
    """)
    List<ChatMessage> findMessagesAfterLeave(Long roomId, String userId);
}
