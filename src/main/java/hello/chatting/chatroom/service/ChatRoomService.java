package hello.chatting.chatroom.service;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.ChatRoomMember;
import hello.chatting.chatroom.domain.Role;
import hello.chatting.chatroom.domain.RoomType;
import hello.chatting.chatroom.dto.ChatRoomDto;
import hello.chatting.chatroom.dto.ChatRoomReqDto;
import hello.chatting.chatroom.repository.ChatRoomMemberRepository;
import hello.chatting.chatroom.repository.ChatRoomRepository;
import hello.chatting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

    // 1:1 방 조회
    public ChatRoom findPrivateRoom(ChatRoomReqDto dto) throws Exception {
        ChatRoom room = chatRoomRepository.findPrivateRoom(dto.getUserId(), dto.getFriendId(), RoomType.PRIVATE);

        if (room == null) {
            room = createPrivateRoom(dto);
        }
        return room;
    }


    // 1:1 방 생성
    @Transactional
    public ChatRoom createPrivateRoom(ChatRoomReqDto dto) throws Exception {

        String userId = dto.getUserId();
        String friendId = dto.getFriendId();

        String userName = userRepository.findByLoginId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음: " + userId))
                .getName();
        String friendName = userRepository.findByLoginId(friendId)
                .orElseThrow(() -> new RuntimeException("사용자 없음: " + friendId))
                .getName();

        // ChatRoom 생성
        ChatRoom room = ChatRoom.builder()
                .type(RoomType.PRIVATE)
                .roomName(userName + ", " + friendName)
                .build();
        room = chatRoomRepository.save(room);

        // 본인 멤버 등록
        ChatRoomMember meMember = ChatRoomMember.builder()
                .roomId(room.getId())
                .userId(dto.getUserId())
                .role(Role.OWNER)
                .build();
        chatRoomMemberRepository.save(meMember);

        // 친구 멤버 등록
        ChatRoomMember friendMember = ChatRoomMember.builder()
                .roomId(room.getId())
                .userId(friendId)
                .build();
        chatRoomMemberRepository.save(friendMember);

        return room;
    }

    public List<ChatRoom> findAllByUserId(String userId) {
        return chatRoomRepository.findAllByUserId(userId);
    }
}

