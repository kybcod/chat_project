package hello.chatting.chatroom.service;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.ChatRoomMember;
import hello.chatting.chatroom.repository.ChatRoomMemberRepository;
import hello.chatting.chatroom.repository.ChatRoomRepository;
import hello.chatting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

    // 1:1 방 조회
    public ChatRoom findPrivateRoom(String me, String friendId) {
        return chatRoomRepository.findPrivateRoom(me, friendId);
    }


    // 1:1 방 생성
    public ChatRoom createPrivateRoom(String me, String friend) {


        String friendName = userRepository.findByLoginId(friend)
                .orElseThrow(() -> new RuntimeException("사용자 없음: " + friend))
                .getName();


        // ChatRoom 생성
        ChatRoom room = ChatRoom.builder()
                .type("PRIVATE")
                .roomName(friendName)
                .build();
        room = chatRoomRepository.save(room);

        // 본인 멤버 등록
        ChatRoomMember meMember = ChatRoomMember.builder()
                .roomId(room.getId())
                .userId(me)
                .role("OWNER")
                .build();
        chatRoomMemberRepository.save(meMember);

        // 친구 멤버 등록
        ChatRoomMember friendMember = ChatRoomMember.builder()
                .roomId(room.getId())
                .userId(friend)
                .build();
        chatRoomMemberRepository.save(friendMember);

        return room;
    }

    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }
}

