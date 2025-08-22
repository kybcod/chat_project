package hello.chatting.chatroom.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class ChatRoom {
    private String id;
    private String roomId;
    private String name;
    private String createdAt;
}
