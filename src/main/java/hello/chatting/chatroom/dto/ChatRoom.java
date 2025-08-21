package hello.chatting.chatroom.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatRoom {
    private String id;
    private String roomId;
    private String name;
    private String createdAt;
}
