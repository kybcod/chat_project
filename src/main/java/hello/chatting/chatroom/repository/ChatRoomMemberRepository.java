package hello.chatting.chatroom.repository;

import hello.chatting.chatroom.domain.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

}
