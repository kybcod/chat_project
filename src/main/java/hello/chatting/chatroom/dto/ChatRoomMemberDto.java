package hello.chatting.chatroom.dto;

import hello.chatting.chatroom.domain.ChatRoomMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMemberDto {
    private Long roomId;
    private String userId; // 알람 받을 사람

    // Entity → DTO 변환
    public static ChatRoomMemberDto toDto(ChatRoomMember member) {
        return ChatRoomMemberDto.builder()
                .roomId(member.getRoomId())
                .userId(member.getUserId())
                .build();
    }
}
