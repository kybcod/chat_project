package hello.chatting.chatroom.dto;

import hello.chatting.chatroom.domain.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomReqDto {
    private String userId;
    private String friendId;
}
