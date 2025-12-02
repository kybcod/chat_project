package hello.chatting.chat.dto;

import hello.chatting.chat.domain.ChatMessage;
import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.dto.ChatRoomDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    private Long id;
    private Long roomId;
    private String sender;
    private String message;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 (화면)
    public static ChatMessageDto toDto(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .roomId(chatMessage.getRoomId())
                .sender(chatMessage.getSender())
                .message(chatMessage.getMessage())
                .build();
    }

    // DTO → Entity 변환
    public static ChatMessage toEntity(ChatMessageDto dto) {
        return ChatMessage.builder()
                .roomId(dto.getRoomId())
                .sender(dto.getSender())
                .message(dto.getMessage())
                .build();
    }
}
