package hello.chatting.chatroom.service;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.ChatRoomMember;
import hello.chatting.chatroom.domain.Role;
import hello.chatting.chatroom.domain.RoomType;
import hello.chatting.chatroom.dto.ChatRoomReqDto;
import hello.chatting.chatroom.dto.RoomWithUsersDto;
import hello.chatting.chatroom.repository.ChatRoomMemberRepository;
import hello.chatting.chatroom.repository.ChatRoomRepository;
import hello.chatting.user.repository.UserRepository;
import hello.chatting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public List<ChatRoomMember> findByRoomIdAndUserIdNot(ChatRoomReqDto dto) {
        return chatRoomMemberRepository.findByRoomIdAndUserIdNot(dto.getRoomId(), dto.getUserId());
    }

    public String getFriendName(ChatRoom chatRoom, String userId) {
        return userService.extractFriendName(chatRoom.getRoomName(), userId);
    }

    public List<RoomWithUsersDto> findRoomByUserIds(List<String> userIds, String requesterId) {
        userIds.add(requesterId);

        List<Object[]> info = chatRoomMemberRepository
                .findRoomAndUsersByExactMembers(userIds, userIds.size());

        Map<Long, RoomWithUsersDto> map = new LinkedHashMap<>();

        for (Object[] row : info) {
            Long roomId = ((Number) row[0]).longValue();
            String roomName = (String) row[1];
            String type = (String) row[2];

            String userId = (String) row[3];
            String name = (String) row[4];
            String email = (String) row[5];
            String profileImage = (String) row[6];
            Long memberCount = ((Number) row[7]).longValue();

            String groupRoomName = userService.extractFriendName(roomName, requesterId);

            if (!userId.equals(requesterId)) {
                map.computeIfAbsent(roomId, id ->
                        new RoomWithUsersDto(id, groupRoomName, type, memberCount,new ArrayList<>())
                ).users().add(new RoomWithUsersDto.UserInfo(
                        userId, name, email, profileImage
                ));
            }
        }

        return new ArrayList<>(map.values());
    }



}

