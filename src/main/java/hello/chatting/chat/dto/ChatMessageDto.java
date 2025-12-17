package hello.chatting.chat.dto;

import hello.chatting.chat.domain.ChatMessage;
import hello.chatting.chatroom.domain.ChatRoom;
import hello.chatting.chatroom.dto.ChatRoomDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessageDto {

    private Long id;
    private Long roomId;
    private String sender;
    private String senderName;
    private String message;
    private String type;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 (화면)
    public static ChatMessageDto toDto(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .roomId(chatMessage.getRoomId())
                .sender(chatMessage.getSender())
                .type(chatMessage.getType())
                .fileUrl(chatMessage.getFileUrl())
                .fileName(chatMessage.getFileName())
                .fileType(chatMessage.getFileType())
                .message(chatMessage.getMessage())
                .build();
    }

    // DTO → Entity 변환
    public static ChatMessage toEntity(ChatMessageDto dto) {
        return ChatMessage.builder()
                .roomId(dto.getRoomId())
                .sender(dto.getSender())
                .type(dto.getType())
                .fileUrl(dto.getFileUrl())
                .fileName(dto.getFileName())
                .fileType(dto.getFileType())
                .message(dto.getMessage())
                .build();
    }
}
