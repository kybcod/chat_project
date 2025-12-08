package hello.chatting.chatroom.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChatRoomReqDto {
    private Long roomId;
    private List<String> userIds;
}
