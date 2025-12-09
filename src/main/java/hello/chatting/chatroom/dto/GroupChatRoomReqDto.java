package hello.chatting.chatroom.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatRoomReqDto {
    private Long roomId;
    private String userId;
    private List<String> userIds;
}
