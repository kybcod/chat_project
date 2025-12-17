package hello.chatting.chatroom.dto;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeleteChatRoomReqDto {
    private Long roomId;
    private String userId;
}
