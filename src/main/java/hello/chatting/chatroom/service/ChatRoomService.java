package hello.chatting.chatroom.service;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.ChatRoomMember;
import hello.chatting.chatroom.domain.Role;
import hello.chatting.chatroom.domain.RoomType;
import hello.chatting.chatroom.dto.ChatRoomReqDto;
import hello.chatting.chatroom.dto.GroupChatRoomReqDto;
import hello.chatting.chatroom.dto.RoomWithUsersDto;
import hello.chatting.chatroom.repository.ChatRoomMemberRepository;
import hello.chatting.chatroom.repository.ChatRoomRepository;
import hello.chatting.user.domain.User;
import hello.chatting.user.repository.UserRepository;
import hello.chatting.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // 친구 클릭 시 1:1 채팅 방 조회
    public ChatRoom findPrivateRoom(ChatRoomReqDto dto) throws Exception {
        ChatRoom room = chatRoomRepository.findPrivateRoom(dto.getUserId(), dto.getFriendId(), RoomType.PRIVATE);

        if (room == null) {
            GroupChatRoomReqDto group = new GroupChatRoomReqDto();
            group.setUserId(dto.getUserId());
            group.setUserIds(List.of(dto.getFriendId()));
            room = createRoom(group);
        }
        return room;
    }


    // 채팅방 생성
    @Transactional
    public ChatRoom createRoom(GroupChatRoomReqDto dto) throws Exception {

        ArrayList<String> members = new ArrayList<>(dto.getUserIds());
        members.add(dto.getUserId());
        Set<String> partIds = new HashSet<>(members);

        RoomType type = partIds.size() == 2 ? RoomType.PRIVATE : RoomType.GROUP;

        String roomName;
        if (dto.getRoomName() != null && !dto.getRoomName().isBlank()) {
            roomName = dto.getRoomName();
        } else {
            roomName = null;
        }

        ChatRoom room = ChatRoom.builder()
                .type(type)
                .roomName(roomName)
                .build();

        chatRoomRepository.save(room);

        for (String id : partIds) {
            chatRoomMemberRepository.save(
                    ChatRoomMember.builder()
                            .roomId(room.getId())
                            .userId(id)
                            .role(id.equals(dto.getUserId()) ? Role.OWNER : Role.MEMBER)
                            .build()
            );
        }

        if (room == null){
            throw new Exception("해닫 채팅방을 찾을 수 없습니다.");
        }

        return room;
    }


    // 로그인한 유저의 채팅방 리스트
    public List<ChatRoom> findAllByUserId(String userId) throws Exception {

        List<ChatRoom> rooms = chatRoomRepository.findAllByUserId(userId);
        if (rooms == null) {
            throw new Exception("채팅 리스트를 불러오기를 실패했습니다.");
        }

        List<ChatRoom> result = new ArrayList<>();

        for (ChatRoom room : rooms) {

            ChatRoom displayRoom;

            if (room.getRoomName() != null && !room.getRoomName().isBlank()) {
                displayRoom = room;
            } else {
                String displayName = convertDisplayRoomName(room.getId(), userId);
                displayRoom = room.toBuilder()
                        .roomName(displayName)
                        .build();
            }

            result.add(displayRoom);
        }


        return result;
    }


    public List<ChatRoomMember> findByRoomIdAndUserIdNot(ChatRoomReqDto dto) {
        return chatRoomMemberRepository.findByRoomIdAndUserIdNot(dto.getRoomId(), dto.getUserId());
    }


    public List<RoomWithUsersDto> findRoomByUserIds(List<String> userIds, String loginUserId) {
        userIds.add(loginUserId);

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

            String groupRoomName = convertDisplayRoomName(roomId, loginUserId);

            if (!userId.equals(loginUserId)) {
                map.computeIfAbsent(roomId, id ->
                        new RoomWithUsersDto(id, groupRoomName, type, memberCount,new ArrayList<>())
                ).users().add(new RoomWithUsersDto.UserInfo(
                        userId, name, email, profileImage
                ));
            }
        }

        return new ArrayList<>(map.values());
    }


    /***
     * roomName 화면 표출 시 채팅방 참여자 이름 나열
     */
    private String convertDisplayRoomName(Long roomId, String loginId) {

        List<User> chatRoomUser = userRepository.findChatRoomUser(roomId);
        String roomName = chatRoomUser.stream()
                .filter(u -> !u.getLoginId().equals(loginId))
                .map(User::getName).collect(Collectors.joining(", "));

        return roomName;
    }


}

