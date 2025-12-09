package hello.chatting.chatroom.service;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.ChatRoomMember;
import hello.chatting.chatroom.domain.Role;
import hello.chatting.chatroom.domain.RoomType;
import hello.chatting.chatroom.dto.ChatRoomReqDto;
import hello.chatting.chatroom.repository.ChatRoomMemberRepository;
import hello.chatting.chatroom.repository.ChatRoomRepository;
import hello.chatting.user.repository.UserRepository;
import hello.chatting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    private final UserService userService;

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

        // ChatRoom 생성
        ChatRoom room = ChatRoom.builder()
                .type(RoomType.PRIVATE)
                .roomName(userId + ", " + friendId)
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

        if (room == null){
            throw new Exception("해닫 채팅방을 찾을 수 없습니다.");
        }

        return room;
    }

    public List<ChatRoom> findAllByUserId(String userId) throws Exception {
        List<ChatRoom> allByUserId = chatRoomRepository.findAllByUserId(userId);
        if (allByUserId == null) {
            throw new Exception("채팅 리스트를 불러오기를 실패했습니다.");
        }
        return allByUserId;
    }

    public ChatRoomMember findByRoomIdAndUserIdNot(ChatRoomReqDto dto) {
        return chatRoomMemberRepository.findByRoomIdAndUserIdNot(dto.getRoomId(), dto.getUserId());
    }

    public String getFriendName(ChatRoom chatRoom, String userId) {
        return userService.extractFriendName(chatRoom.getRoomName(), userId);
    }

    public List<ChatRoom> findRoomByUserIds(List<String> userIds) {
        return chatRoomMemberRepository
                .findRoomsByExactMembers(userIds, userIds.size());
    }


}

