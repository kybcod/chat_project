package hello.chatting.chatroom.service;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.ChatRoomMember;
import hello.chatting.chatroom.repository.ChatRoomMemberRepository;
import hello.chatting.chatroom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    // 1:1 방 조회
    public ChatRoom findPrivateRoom(String me, String friendId) {
        return chatRoomRepository.findPrivateRoom(me, friendId);
    }

    // 1:1 방 생성
    public ChatRoom createPrivateRoom(String me, String friend) {

        // 1️⃣ ChatRoom 생성
        ChatRoom room = ChatRoom.builder()
                .type("PRIVATE")
                .roomName(null)
                .build();
        room = chatRoomRepository.save(room); // DB에 insert 후 id 획득

        // 2️⃣ ChatRoomMember 생성 (나)
        ChatRoomMember meMember = ChatRoomMember.builder()
                .roomId(room.getId())
                .userId(me)
                .role("OWNER")
                .build();
        chatRoomMemberRepository.save(meMember);

        // 3️⃣ ChatRoomMember 생성 (친구)
        ChatRoomMember friendMember = ChatRoomMember.builder()
                .roomId(room.getId())
                .userId(friend)
                .build();
        chatRoomMemberRepository.save(friendMember);

        return room;
    }
}

