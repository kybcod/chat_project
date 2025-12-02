package hello.chatting.chatroom.dto;

import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.domain.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;             // BIGINT PK
    private RoomType type;         // PRIVATE / GROUP
    private String roomName;     // 그룹방 이름, 1:1은 null
    private LocalDateTime createdAt;

    // Entity → DTO 변환 (화면)
    public static ChatRoomDto toDto(ChatRoom chatRoom, String userId) {
        String friendName = extractFriendName(chatRoom.getRoomName(), userId);
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .type(chatRoom.getType())
                .roomName(friendName)  // 친구 이름만 표시
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    // roomName에서 친구 이름 추출하는 메서드
    private static String extractFriendName(String roomName, String userId) {
        String[] names = roomName.split(", ");
        // userId에 해당하는 이름을 제외한 친구의 이름을 반환
        if (names[0].equals(userId)) {
            return names[0];
        } else {
            return names[1];
        }
    }

    // DTO → Entity 변환
    public static ChatRoom toEntity(ChatRoomDto chatRoom) {
        return ChatRoom.builder()
                .id(chatRoom.getId())
                .type(chatRoom.getType())
                .roomName(chatRoom.getRoomName())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
